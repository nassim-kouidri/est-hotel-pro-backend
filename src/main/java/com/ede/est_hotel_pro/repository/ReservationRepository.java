package com.ede.est_hotel_pro.repository;

import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import com.ede.est_hotel_pro.entity.reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, UUID> {

    List<ReservationEntity> findAllByEndDateBefore(Instant now);

    List<ReservationEntity> findAllByStartDateAfterAndStatus(Instant startDate, ReservationStatus status);

    List<ReservationEntity> findAllByHotelRoom_IdAndStartDateLessThanAndEndDateGreaterThan(UUID roomId, Instant endDate, Instant startDate);

    List<ReservationEntity> findAllByStatus(ReservationStatus status);

    List<ReservationEntity> findAllByStatusIn(List<ReservationStatus> status);

    List<ReservationEntity> findAllByCompleted(boolean completed);
}
