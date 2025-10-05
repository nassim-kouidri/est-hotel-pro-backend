package com.ede.est_hotel_pro.controller;

import com.ede.est_hotel_pro.dto.create.CreateReservationRequest;
import com.ede.est_hotel_pro.dto.out.DailyReservationsResponse;
import com.ede.est_hotel_pro.dto.out.MonthlyCalendarResponse;
import com.ede.est_hotel_pro.dto.out.ReservationChartResponse;
import com.ede.est_hotel_pro.dto.out.ReservationResponse;
import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import com.ede.est_hotel_pro.entity.reservation.ReservationStatus;
import com.ede.est_hotel_pro.entity.reservation.PaymentStatus;
import com.ede.est_hotel_pro.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "reservations", description = "Route to manipulate reservations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/filter/pageable")
    public Page<ReservationResponse> getReservationsPageable(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) UUID hotelRoomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @ParameterObject Pageable pageable) {

        Page<ReservationEntity> reservationsPage = reservationService.findAllReservationsByFilterPageable(status, paymentStatus, companyName, hotelRoomId, startDate, endDate, pageable);
        return reservationsPage.map(ReservationResponse::toDto);
    }

    @GetMapping("/charts")
    public List<ReservationChartResponse> getAllReservationsForChart() {
        return reservationService.findAllReservationsForChart();
    }

    @GetMapping("/{id}")
    public ReservationResponse getReservationById(@PathVariable UUID id) {
        return ReservationResponse.toDto(reservationService.findById(id));
    }

    @PostMapping
    public ReservationResponse createReservation(@RequestBody CreateReservationRequest request) {
        ReservationEntity reservation = reservationService.save(request);
        return ReservationResponse.toDto(reservation);
    }

    @PutMapping("/{id}")
    public ReservationResponse updateReservation(@PathVariable UUID id, @RequestBody CreateReservationRequest request) {
        ReservationEntity reservation = reservationService.update(id, request);
        return ReservationResponse.toDto(reservation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(com.ede.est_hotel_pro.entity.account.Role).ADMIN)")
    public void deleteReservationById(@PathVariable UUID id) {
        reservationService.deleteById(id);
    }

    @GetMapping("/calendar/monthly")
    @Operation(summary = "Get reservation counts for each day in a month")
    public MonthlyCalendarResponse getMonthlyCalendar(
            @RequestParam int year,
            @RequestParam int month) {
        return reservationService.getMonthlyCalendar(year, month);
    }

    @GetMapping("/calendar/daily")
    @Operation(summary = "Get detailed reservations for a specific date")
    public DailyReservationsResponse getDailyReservations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reservationService.getDailyReservations(date);
    }

    @GetMapping("/companies")
    @Operation(summary = "Get unique list of contracted companies (normalized, alphabetically sorted)")
    public List<String> getCompanies() {
        return reservationService.getDistinctNormalizedCompanies();
    }
}
