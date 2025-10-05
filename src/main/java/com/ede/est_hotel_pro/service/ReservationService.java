package com.ede.est_hotel_pro.service;

import com.ede.est_hotel_pro.dto.create.CreateReservationRequest;
import com.ede.est_hotel_pro.dto.out.DailyReservationsResponse;
import com.ede.est_hotel_pro.dto.out.MonthlyCalendarResponse;
import com.ede.est_hotel_pro.dto.out.ReservationChartResponse;
import com.ede.est_hotel_pro.dto.out.ReservationResponse;
import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;
import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import com.ede.est_hotel_pro.entity.reservation.ReservationStatus;
import com.ede.est_hotel_pro.entity.reservation.PaymentStatus;
import com.ede.est_hotel_pro.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HotelRoomService hotelRoomService;

    public Page<ReservationEntity> findAllReservationsByFilterPageable(ReservationStatus status, PaymentStatus paymentStatus, String companyName, UUID hotelRoomId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return reservationRepository.findAllReservationsByFilterPageable(status, paymentStatus, companyName, hotelRoomId, startDate, endDate, pageable);
    }

    public List<ReservationChartResponse> findAllReservationsForChart() {
        return reservationRepository.findAllReservationsForChart();
    }

    public ReservationEntity findById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("The reservation with id '%s' was not found", id)));
    }

    @Transactional
    public ReservationEntity save(CreateReservationRequest request) {
        checkCreateAndUpdateReservation(request);
        HotelRoomEntity roomEntity = hotelRoomService.findById(request.roomId());

        String normalizedCompany = (request.isContracted() && request.companyName() != null)
                ? normalizeCompanyName(request.companyName())
                : null;

        ReservationEntity reservation = new ReservationEntity().toBuilder()
                .startDate(request.startDate())
                .endDate(request.endDate())
                .hotelRoom(roomEntity)
                .userReservation(request.userSnapshot())
                .pricePaid(request.pricePaid())
                .numberOfChildren(request.numberOfChildren())
                .numberOfAdults(request.numberOfAdults())
                .claim(request.claim())
                .review(request.review())
                .status(handleReservationStatus(request.startDate(), request.endDate()))
                .isContracted(request.isContracted())
                .companyName(normalizedCompany)
                .paymentStatus(request.paymentStatus() != null ? request.paymentStatus() : PaymentStatus.FULLY_PAID)
                .paymentRemark(request.paymentRemark())
                .build();

        reservation.setCompleted(reservation.isReservationFinished());
        if (reservation.isReservationInProgress() && roomEntity.isAvailable()) {
            roomEntity.setAvailable(false);
            hotelRoomService.updateAvailability(roomEntity);
        }

        return reservationRepository.save(reservation);
    }

    @Transactional
    public ReservationEntity update(UUID id, CreateReservationRequest request) {
        ReservationEntity existingReservation = findById(id);

        existingReservation.setNumberOfChildren(request.numberOfChildren());
        existingReservation.setNumberOfAdults(request.numberOfAdults());
        existingReservation.setUserReservation(request.userSnapshot());
        existingReservation.setPricePaid(request.pricePaid());
        existingReservation.setClaim(request.claim());
        existingReservation.setReview(request.review());
        existingReservation.setContracted(request.isContracted());
        String normalizedCompany = (request.isContracted() && request.companyName() != null)
                ? normalizeCompanyName(request.companyName())
                : null;
        existingReservation.setCompanyName(normalizedCompany);
        existingReservation.setPaymentStatus(request.paymentStatus() != null ? request.paymentStatus() : PaymentStatus.FULLY_PAID);
        existingReservation.setPaymentRemark(request.paymentRemark());

        return reservationRepository.save(existingReservation);
    }

    @Transactional
    public void deleteById(UUID id) {
        ReservationEntity reservation = findById(id);
        HotelRoomEntity roomEntity = reservation.getHotelRoom();
        reservationRepository.delete(reservation);
        if (reservation.isReservationInProgress() && !roomEntity.isAvailable()) {
            roomEntity.setAvailable(true);
            hotelRoomService.updateAvailability(roomEntity);
        }
    }

    private void checkCreateAndUpdateReservation(CreateReservationRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("The end date must be after the start date.");
        }
        if (!isRoomAvailableBetweenDates(request.roomId(), request.startDate(), request.endDate())) {
            throw new IllegalArgumentException("The room is not available during the requested period.");
        }
        if (request.isContracted() && (request.companyName() == null || request.companyName().trim().isEmpty())) {
            throw new IllegalArgumentException("Company name is required for contracted clients.");
        }
        // Payment rules
        PaymentStatus status = request.paymentStatus() != null ? request.paymentStatus() : PaymentStatus.FULLY_PAID;
        String remark = request.paymentRemark();
        if (status == PaymentStatus.FULLY_PAID) {
            if (remark != null && !remark.trim().isEmpty()) {
                throw new IllegalArgumentException("Payment remark must be empty when payment status is FULLY_PAID.");
            }
        } else { // PARTIALLY_PAID or NOT_PAID
            if (remark == null || remark.trim().isEmpty()) {
                throw new IllegalArgumentException("Payment remark is required when payment is partially or not paid.");
            }
        }
    }

    private boolean isRoomAvailableBetweenDates(UUID roomId, Instant startDate, Instant endDate) {
        List<ReservationEntity> overlappingReservations =
                reservationRepository.findAllByHotelRoom_IdAndStartDateLessThanAndEndDateGreaterThan(roomId, endDate, startDate);
        return overlappingReservations.isEmpty();
    }

    private ReservationStatus handleReservationStatus(Instant startDate, Instant endDate) {
        Instant now = Instant.now();
        if (startDate.isBefore(now) && endDate.isAfter(now)) {
            return ReservationStatus.IN_PROGRESS;
        }
        if (endDate.isBefore(now)) {
            return ReservationStatus.ENDED;
        }
        if (startDate.isAfter(now)) {
            return ReservationStatus.COMING;
        }
        throw new IllegalStateException("Invalid reservation dates");
    }

    private String normalizeCompanyName(String name) {
        if (name == null) return null;
        // Trim, collapse multiple spaces, lower-case
        String cleaned = name.trim().replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) return "";
        // Title-case words and handle hyphenated parts
        StringBuilder sb = new StringBuilder();
        String[] words = cleaned.toLowerCase().split(" ");
        for (int i = 0; i < words.length; i++) {
            if (i > 0) sb.append(' ');
            String w = words[i];
            String[] hyphenParts = w.split("-");
            for (int j = 0; j < hyphenParts.length; j++) {
                String part = hyphenParts[j];
                if (!part.isEmpty()) {
                    sb.append(Character.toUpperCase(part.charAt(0)));
                    if (part.length() > 1) sb.append(part.substring(1));
                }
                if (j < hyphenParts.length - 1) sb.append('-');
            }
        }
        return sb.toString();
    }


    @Scheduled(cron = "0 0 * * * *") // Every hour
//    @Scheduled(cron = "0 */1 * * * *") // Every 1 minute
    @Transactional
    protected void updateRoomAvailabilityBasedOnReservations() {
        List<ReservationEntity> reservationEntitiesToUpdate = reservationRepository.findAllByCompleted(false);
        for (ReservationEntity reservation : reservationEntitiesToUpdate) {
            ReservationStatus newStatus = handleReservationStatus(reservation.getStartDate(), reservation.getEndDate());
            if (reservation.getStatus() != newStatus) {
                reservation.setStatus(handleReservationStatus(reservation.getStartDate(), reservation.getEndDate()));
                HotelRoomEntity room = reservation.getHotelRoom();
                if (reservation.isReservationInProgress() && room.isAvailable()) {
                    room.setAvailable(false);
                    hotelRoomService.updateAvailability(room);
                }
                if (reservation.isReservationFinished()) {
                    reservation.setCompleted(true);
                    if (!room.isAvailable()) {
                        room.setAvailable(true);
                        hotelRoomService.updateAvailability(room);
                    }
                }
                reservationRepository.save(reservation);
            }
        }
    }

    /**
     * Get a monthly calendar with reservation counts for each day.
     *
     * @param year  The year
     * @param month The month (1-12)
     * @return A MonthlyCalendarResponse containing reservation counts for each day
     */
    public MonthlyCalendarResponse getMonthlyCalendar(int year, int month) {
        List<Object[]> dailyCounts = reservationRepository.countReservationsByDayInMonth(year, month);

        // Convert the list of Object[] to a Map<Integer, Integer>
        Map<Integer, Integer> dailyReservationCounts = new HashMap<>();
        for (Object[] result : dailyCounts) {
            Integer day = ((Number) result[0]).intValue();
            Integer count = ((Number) result[1]).intValue();
            dailyReservationCounts.put(day, count);
        }

        return MonthlyCalendarResponse.create(year, month, dailyReservationCounts);
    }

    /**
     * Get detailed information about reservations for a specific date.
     *
     * @param date The date to get reservations for
     * @return A DailyReservationsResponse containing detailed information about reservations
     */
    public DailyReservationsResponse getDailyReservations(LocalDate date) {
        List<ReservationEntity> reservations = reservationRepository.findAllReservationsForDate(date);
        List<ReservationResponse> reservationResponses = reservations.stream()
                .map(ReservationResponse::toDto)
                .toList();

        return DailyReservationsResponse.create(date, reservationResponses);
    }

    /**
     * Return unique, normalized company names for contracted reservations, sorted alphabetically.
     */
    public List<String> getDistinctNormalizedCompanies() {
        List<String> raw = reservationRepository.findDistinctCompanyNamesForContracted();
        return raw.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .map(this::normalizeCompanyName)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

}
