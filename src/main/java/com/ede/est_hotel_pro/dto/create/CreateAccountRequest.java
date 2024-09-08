package com.ede.est_hotel_pro.dto.create;

import com.ede.est_hotel_pro.entity.account.Role;

public record CreateAccountRequest(
        String name,
        String firstName,
        Role role,
        String phoneNumber,
        String password) {
}
