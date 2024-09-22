package com.ede.est_hotel_pro.dto.create;

import com.ede.est_hotel_pro.entity.reservation.UserSnapshot;

import java.time.Instant;

public record CreateReservationRequest(
        UserSnapshot userSnapshot,
        Instant startDate,
        Instant endDate,
        String claim,
        int numberOfChildren,
        int numberOfAdults,
        int pricePaid,
        long review
        ) {
}
