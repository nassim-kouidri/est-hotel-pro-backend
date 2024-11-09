package com.ede.est_hotel_pro.service;

import com.ede.est_hotel_pro.dto.create.CreateReservationRequest;
import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;
import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import com.ede.est_hotel_pro.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HotelRoomService hotelRoomService;

    public List<ReservationEntity> findAll() {
        return reservationRepository.findAll();
    }

    public ReservationEntity findById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("The reservation with id '%s' was not found", id)));

    }

    @Transactional
    public ReservationEntity save(CreateReservationRequest request) {
        HotelRoomEntity roomEntity = hotelRoomService.findById(request.roomId());
        if (!roomEntity.isAvailable()) {
            throw new IllegalArgumentException("The room is not available for reservation.");
        }
        roomEntity.setAvailable(false);
        hotelRoomService.update(roomEntity);

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
                .build();

        return reservationRepository.save(reservation);
    }

    @Transactional
    public void deleteById(UUID id) {
        ReservationEntity reservation = findById(id);
        HotelRoomEntity roomEntity = reservation.getHotelRoom();

        reservationRepository.delete(reservation);

        roomEntity.setAvailable(true);
        hotelRoomService.update(roomEntity);
    }

    @Scheduled(cron = "0 0 10 * * *") // every day at 10am
    @Transactional
    public void releaseRoomsAfterEndDate() {
        List<ReservationEntity> expiredReservations = reservationRepository.findAllByEndDateBefore(Instant.now());

        for (ReservationEntity reservation : expiredReservations) {
            HotelRoomEntity room = reservation.getHotelRoom();

            if (!room.isAvailable()) {
                room.setAvailable(true);
                hotelRoomService.update(room);
            }
        }
    }
}
