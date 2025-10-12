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

    List<ReservationEntity> findAllByHotelRoom_IdAndStartDateLessThanAndEndDateGreaterThan(UUID roomId, Instant endDate, Instant startDate);

    List<ReservationEntity> findAllByCompleted(boolean completed);

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
            AND (:companyName IS NULL OR r.companyName = :companyName)
            AND (COALESCE(:hotelRoomId, r.hotelRoom.id) = r.hotelRoom.id)
            AND FUNCTION('DATE', r.startDate) <= :endDate
            AND FUNCTION('DATE', r.endDate) >= :startDate
            ORDER BY r.startDate ASC
            """)
    Page<ReservationEntity> findAllReservationsByFilterPageable(
            @Param("status") ReservationStatus status,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("companyName") String companyName,
            @Param("hotelRoomId") UUID hotelRoomId,
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

    @Query("""
            SELECT DISTINCT r.companyName
            FROM ReservationEntity r
            WHERE r.isContracted = TRUE
              AND r.companyName IS NOT NULL
              AND r.companyName <> ''
            """)
    List<String> findDistinctCompanyNamesForContracted();

    // --- Statistics ---
    @Query("""
            SELECT r
            FROM ReservationEntity r
            WHERE r.startDate < :end AND r.endDate > :start
            """)
    List<ReservationEntity> findOverlapping(@Param("start") Instant start, @Param("end") Instant end);

    @Query(value = """
            WITH days AS (
              SELECT CAST(generate_series(CAST(:start AS date), CAST(:end AS date) - INTERVAL '1 day', INTERVAL '1 day') AS date) AS d
            )
            SELECT d.d AS date, COUNT(r.id) AS occupied_room_nights
            FROM days d
            LEFT JOIN reservation r
              ON r.start_date <= d.d + INTERVAL '1 day'
             AND r.end_date   >  d.d
            GROUP BY d.d
            ORDER BY d.d
            """, nativeQuery = true)
    List<Object[]> dailyOccupiedRoomNights(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query(value = """
            SELECT DATE(r.start_date) AS date, COUNT(r.id) AS reservations
            FROM reservation r
            WHERE DATE(r.start_date) >= :start AND DATE(r.start_date) < :end
            GROUP BY DATE(r.start_date)
            ORDER BY DATE(r.start_date)
            """, nativeQuery = true)
    List<Object[]> dailyReservationCounts(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query(value = """
            WITH days AS (
              SELECT CAST(generate_series(CAST(:start AS date), CAST(:end AS date) - INTERVAL '1 day', INTERVAL '1 day') AS date) AS d
            ), over AS (
              SELECT id,
                     GREATEST(DATE(start_date), :start) AS s,
                     LEAST(DATE(end_date),   :end)     AS e,
                     CAST((LEAST(DATE(end_date), :end) - GREATEST(DATE(start_date), :start)) AS int) AS nights_in_range,
                     CAST((DATE(end_date) - DATE(start_date)) AS int) AS total_nights,
                     price_paid
              FROM reservation
              WHERE start_date < CAST(:end AS date) AND end_date > CAST(:start AS date)
            )
            SELECT d.d AS date,
                   COALESCE(SUM(CASE WHEN o.total_nights > 0 THEN CAST(o.price_paid AS numeric) / o.total_nights ELSE 0 END), 0) AS revenue
            FROM days d
            LEFT JOIN over o ON d.d >= o.s AND d.d < o.e
            GROUP BY d.d
            ORDER BY d.d
            """, nativeQuery = true)
    List<Object[]> dailyRevenue(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
            SELECT r.paymentStatus AS paymentStatus, COUNT(r) AS cnt, SUM(r.pricePaid) AS revenue
            FROM ReservationEntity r
            WHERE r.startDate < :end AND r.endDate > :start
            GROUP BY r.paymentStatus
            """)
    List<Object[]> paymentStatusAggregate(@Param("start") Instant start, @Param("end") Instant end);
}
