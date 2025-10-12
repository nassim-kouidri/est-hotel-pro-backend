package com.ede.est_hotel_pro.dto.out.statistics;

import com.ede.est_hotel_pro.entity.reservation.PaymentStatus;

public record PaymentStatusSlice(
    PaymentStatus paymentStatus,
    long count,
    double revenue
) {}
