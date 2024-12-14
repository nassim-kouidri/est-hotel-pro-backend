package com.ede.est_hotel_pro.dto.out;

import java.time.Instant;
import java.util.UUID;

public record ReservationChartResponse(UUID id, Instant startDate) {
}
