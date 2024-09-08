package com.ede.est_hotel_pro.dto.out;

import com.ede.est_hotel_pro.entity.account.AccountEntity;
import com.ede.est_hotel_pro.entity.account.Role;

import java.util.UUID;

public record AccountResponse(
        UUID id,
        String name,
        String firstName,
        Role role,
        String phoneNumber) {

    public static AccountResponse toDto(AccountEntity accountEntity) {
        return new AccountResponse(
                accountEntity.getId(),
                accountEntity.getName(),
                accountEntity.getFirstName(),
                accountEntity.getRole(),
                accountEntity.getPhoneNumber());
    }
}
