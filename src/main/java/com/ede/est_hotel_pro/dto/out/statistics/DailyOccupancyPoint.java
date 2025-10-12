package com.ede.est_hotel_pro.dto.out.statistics;

import java.time.LocalDate;

public record DailyOccupancyPoint(
    LocalDate date,
    long occupiedRoomNights,
    double occupancyRate
) {}
