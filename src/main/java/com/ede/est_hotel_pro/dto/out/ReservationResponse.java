package com.ede.est_hotel_pro.dto.out;

import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import com.ede.est_hotel_pro.entity.reservation.UserSnapshot;

import java.time.Instant;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        UserSnapshot userSnapshot,
        Instant startDate,
        Instant endDate,
        String claim,
        int numberOfChildren,
        int numberOfAdults,
        int pricePaid,
        long review
) {
    public static ReservationResponse toDto(ReservationEntity reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUserReservation(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getClaim(),
                reservation.getNumberOfChildren(),
                reservation.getNumberOfAdults(),
                reservation.getPricePaid(),
                reservation.getReview()
        );
    }
}
