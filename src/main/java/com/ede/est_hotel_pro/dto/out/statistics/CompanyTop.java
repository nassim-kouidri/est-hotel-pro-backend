package com.ede.est_hotel_pro.dto.out.statistics;

public record CompanyTop(
    String companyName,
    long reservations,
    double revenue,
    long roomNights
) {}
