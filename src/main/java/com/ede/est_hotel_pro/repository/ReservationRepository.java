package com.ede.est_hotel_pro.repository;

import com.ede.est_hotel_pro.dto.out.ReservationChartResponse;
import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import com.ede.est_hotel_pro.entity.reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT r FROM ReservationEntity r WHERE :status IS NULL OR r.status = :status")
    List<ReservationEntity> findAllByStatusOrAll(@Param("status") ReservationStatus status);

    @Query("""
            SELECT r 
            FROM ReservationEntity r 
            WHERE (:status IS NULL OR r.status = :status)
            ORDER BY r.createdAt DESC
            """)
    List<ReservationEntity> findAllByStatusFilter(@Param("status") ReservationStatus status);

    @Query("""
            SELECT new com.ede.est_hotel_pro.dto.out.ReservationChartResponse(r.id, r.startDate)
            FROM ReservationEntity r
            ORDER BY r.startDate ASC
            """)
    List<ReservationChartResponse> findAllReservationsForChart();

}
