package com.ede.est_hotel_pro.repository;

import com.ede.est_hotel_pro.dto.out.ReservationChartResponse;
import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import com.ede.est_hotel_pro.entity.reservation.ReservationStatus;
import com.ede.est_hotel_pro.entity.reservation.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, UUID> {

    List<ReservationEntity> findAllByEndDateBefore(Instant now);

    List<ReservationEntity> findAllByStartDateAfterAndStatus(Instant startDate, ReservationStatus status);

    List<ReservationEntity> findAllByHotelRoom_IdAndStartDateLessThanAndEndDateGreaterThan(UUID roomId, Instant endDate, Instant startDate);

    List<ReservationEntity> findAllByStatus(ReservationStatus status);

    List<ReservationEntity> findAllByCompleted(boolean completed);

    @Query("SELECT r FROM ReservationEntity r WHERE :status IS NULL OR r.status = :status")
    List<ReservationEntity> findAllByStatusOrAll(@Param("status") ReservationStatus status);


    @Query("""
            SELECT new com.ede.est_hotel_pro.dto.out.ReservationChartResponse(r.id, r.startDate)
            FROM ReservationEntity r
            ORDER BY r.startDate ASC
            """)
    List<ReservationChartResponse> findAllReservationsForChart();

    @Query("""
            SELECT r
            FROM ReservationEntity r
            WHERE :date BETWEEN FUNCTION('DATE', r.startDate) AND FUNCTION('DATE', r.endDate)
            ORDER BY r.startDate ASC
            """)
    List<ReservationEntity> findAllReservationsForDate(@Param("date") LocalDate date);

    @Query("""
            SELECT r 
            FROM ReservationEntity r 
            WHERE (:status IS NULL OR r.status = :status)
            AND (:paymentStatus IS NULL OR r.paymentStatus = :paymentStatus)
            AND FUNCTION('DATE', r.startDate) <= :endDate
            AND FUNCTION('DATE', r.endDate) >= :startDate
            ORDER BY r.startDate ASC
            """)
    Page<ReservationEntity> findAllReservationsByFilterPageable(
            @Param("status") ReservationStatus status,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    /**
     * Count the number of reservations for each day in a month.
     * Returns a map of day of month to reservation count.
     *
     * @param year  The year
     * @param month The month (1-12)
     * @return Map of day of month to reservation count
     */
    @Query("""
            SELECT EXTRACT(DAY FROM FUNCTION('DATE', r.startDate)) as day, COUNT(r.id) as count
            FROM ReservationEntity r
            WHERE EXTRACT(YEAR FROM FUNCTION('DATE', r.startDate)) = :year
            AND EXTRACT(MONTH FROM FUNCTION('DATE', r.startDate)) = :month
            GROUP BY EXTRACT(DAY FROM FUNCTION('DATE', r.startDate))
            """)
    List<Object[]> countReservationsByDayInMonth(@Param("year") int year, @Param("month") int month);
}
