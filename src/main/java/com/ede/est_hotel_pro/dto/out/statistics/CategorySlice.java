package com.ede.est_hotel_pro.dto.out.statistics;

public record CategorySlice(
    String category,
    long roomNights,
    long reservations
) {}
