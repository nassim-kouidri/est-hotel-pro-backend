package com.ede.est_hotel_pro.dto.out;

import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import com.ede.est_hotel_pro.entity.reservation.ReservationStatus;
import com.ede.est_hotel_pro.entity.reservation.UserSnapshot;

import java.time.Instant;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        UserSnapshot userSnapshot,
        HotelRoomResponse hotelRoom,
        Instant startDate,
        Instant endDate,
        String claim,
        int numberOfChildren,
        int numberOfAdults,
        int pricePaid,
        long review,
        ReservationStatus status
) {
    public static ReservationResponse toDto(ReservationEntity reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUserReservation(),
                HotelRoomResponse.toDtoWithoutReservations(reservation.getHotelRoom()),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getClaim(),
                reservation.getNumberOfChildren(),
                reservation.getNumberOfAdults(),
                reservation.getPricePaid(),
                reservation.getReview(),
                reservation.getStatus()
        );
    }

    public static ReservationResponse toDtoWithoutRoom(ReservationEntity reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUserReservation(),
                null,
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getClaim(),
                reservation.getNumberOfChildren(),
                reservation.getNumberOfAdults(),
                reservation.getPricePaid(),
                reservation.getReview(),
                reservation.getStatus()
        );
    }
}
