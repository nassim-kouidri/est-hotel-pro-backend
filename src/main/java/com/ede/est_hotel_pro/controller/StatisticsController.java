package com.ede.est_hotel_pro.controller;

import com.ede.est_hotel_pro.dto.out.statistics.*;
import com.ede.est_hotel_pro.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/statistics")
@Tag(name = "statistics", description = "Statistics endpoints")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    @Operation(summary = "KPIs agrégés sur une plage de dates")
    public OverviewStatsResponse overview(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        validateRange(startDate, endDate);
        return statisticsService.getOverview(startDate, endDate);
    }

    @GetMapping("/series/occupancy")
    @Operation(summary = "Occupation quotidienne (%) et room-nights")
    public List<DailyOccupancyPoint> dailyOccupancy(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        validateRange(startDate, endDate);
        return statisticsService.getDailyOccupancy(startDate, endDate);
    }

    @GetMapping("/series/reservations")
    @Operation(summary = "Réservations/jour et revenus/jour")
    public List<DailyReservationPoint> dailyReservations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        validateRange(startDate, endDate);
        return statisticsService.getDailyReservations(startDate, endDate);
    }

    @GetMapping("/slices/category")
    @Operation(summary = "Répartition par catégorie de chambre")
    public List<CategorySlice> categorySlice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        validateRange(startDate, endDate);
        return statisticsService.getCategorySlice(startDate, endDate);
    }

    @GetMapping("/slices/payment-status")
    @Operation(summary = "Répartition par statut de paiement")
    public List<PaymentStatusSlice> paymentStatusSlice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        validateRange(startDate, endDate);
        return statisticsService.getPaymentStatusSlice(startDate, endDate);
    }

    @GetMapping("/top/companies")
    @Operation(summary = "Top entreprises par revenu/nuitées")
    public List<CompanyTop> topCompanies(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "5") int limit
    ) {
        validateRange(startDate, endDate);
        int safeLimit = Math.max(1, Math.min(20, limit));
        return statisticsService.getTopCompanies(startDate, endDate, safeLimit);
    }

    private void validateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null || end.isBefore(start)) {
            throw new IllegalArgumentException("Invalid date range");
        }
        // Prevent excessively large ranges (e.g., > 2 years) to protect resources
        if (start.plusYears(2).isBefore(end)) {
            throw new IllegalArgumentException("Date range too large");
        }
    }
}
