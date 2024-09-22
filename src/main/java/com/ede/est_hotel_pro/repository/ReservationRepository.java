package com.ede.est_hotel_pro.repository;

import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, UUID> {
}
