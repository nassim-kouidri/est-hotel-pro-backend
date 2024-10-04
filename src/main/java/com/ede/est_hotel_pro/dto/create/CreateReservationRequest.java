package com.ede.est_hotel_pro.dto.create;

import com.ede.est_hotel_pro.entity.reservation.UserSnapshot;

import java.time.Instant;
import java.util.UUID;

public record CreateReservationRequest(
        UserSnapshot userSnapshot,
        Instant startDate,
        UUID roomId,
        Instant endDate,
        String claim,
        int numberOfChildren,
        int numberOfAdults,
        int pricePaid,
        long review
) {
}
