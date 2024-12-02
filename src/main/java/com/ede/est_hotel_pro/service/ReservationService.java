package com.ede.est_hotel_pro.service;

import com.ede.est_hotel_pro.dto.create.CreateReservationRequest;
import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;
import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import com.ede.est_hotel_pro.entity.reservation.ReservationStatus;
import com.ede.est_hotel_pro.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HotelRoomService hotelRoomService;

    public List<ReservationEntity> findAll() {
        return reservationRepository.findAll();
    }

    public List<ReservationEntity> findAllByStatus(ReservationStatus status) {
        return reservationRepository.findAllByStatus(status);
    }

    public List<ReservationEntity> findReservationsByFilter(ReservationStatus status, UUID hotelRoomId) {
        List<ReservationEntity> reservations = reservationRepository.findAllByStatusFilter(status);
        if (hotelRoomId != null) {
            reservations = reservations.stream()
                    .filter(reservation -> reservation.getHotelRoom().getId().equals(hotelRoomId))
                    .toList();
        }
        return reservations;
    }

    public List<ReservationEntity> findAllReservations(Optional<ReservationStatus> status) {
        return reservationRepository.findAllByStatusOrAll(status.orElse(null));
    }

    public ReservationEntity findById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("The reservation with id '%s' was not found", id)));
    }

    @Transactional
    public ReservationEntity save(CreateReservationRequest request) {
        checkCreateAndUpdateReservation(request);
        HotelRoomEntity roomEntity = hotelRoomService.findById(request.roomId());

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


    //    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Scheduled(cron = "0 */2 * * * *") // Every 2 minutes
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
}
