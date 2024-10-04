package com.ede.est_hotel_pro.controller;

import com.ede.est_hotel_pro.dto.create.CreateReservationRequest;
import com.ede.est_hotel_pro.dto.out.ReservationResponse;
import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import com.ede.est_hotel_pro.service.ReservationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "reservations", description = "Route to manipulate reservations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{id}")
    public ReservationResponse getReservationById(@PathVariable UUID id) {
        return ReservationResponse.toDto(reservationService.findById(id));
    }

    @GetMapping
    public List<ReservationResponse> getAllReservations() {
        return reservationService.findAll().stream().map(ReservationResponse::toDto).toList();
    }

    @PostMapping
    public ReservationResponse createReservation(@RequestBody CreateReservationRequest request) {
        ReservationEntity reservation = reservationService.save(request);
        return ReservationResponse.toDto(reservation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(com.ede.est_hotel_pro.entity.account.Role).ADMIN)")
    public void deleteReservationById(@PathVariable UUID id) {
        reservationService.deleteById(id);
    }
}
