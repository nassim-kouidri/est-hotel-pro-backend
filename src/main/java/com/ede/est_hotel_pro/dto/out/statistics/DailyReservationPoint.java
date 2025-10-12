package com.ede.est_hotel_pro.dto.out.statistics;

import java.time.LocalDate;

public record DailyReservationPoint(
    LocalDate date,
    long reservations,
    double revenue
) {}
