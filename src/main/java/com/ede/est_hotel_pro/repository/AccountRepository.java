package com.ede.est_hotel_pro.repository;

import com.ede.est_hotel_pro.entity.account.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByName(String name);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByName(String name);

}
