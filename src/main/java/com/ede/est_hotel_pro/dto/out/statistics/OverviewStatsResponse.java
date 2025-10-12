package com.ede.est_hotel_pro.dto.out.statistics;

public record OverviewStatsResponse(
    long totalReservations,
    double revenue,
    long occupiedRoomNights,
    long roomCapacityNights,
    double occupancyRate,
    double adr,
    double revpar,
    double avgLengthOfStay,
    double contractedShare,
    double contractedRevenue
) {}
