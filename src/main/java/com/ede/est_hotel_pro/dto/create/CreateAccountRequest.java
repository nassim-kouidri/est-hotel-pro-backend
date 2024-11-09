package com.ede.est_hotel_pro.dto.create;

public record CreateAccountRequest(
        String name,
        String firstName,
        String phoneNumber,
        String password) {
}
