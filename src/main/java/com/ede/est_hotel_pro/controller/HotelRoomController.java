package com.ede.est_hotel_pro.controller;

import com.ede.est_hotel_pro.dto.create.CreateRoomRequest;
import com.ede.est_hotel_pro.dto.out.HotelRoomResponse;
import com.ede.est_hotel_pro.entity.hotelroom.CategoryRoom;
import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;
import com.ede.est_hotel_pro.service.HotelRoomService;
import io.swagger.v3.oas.annotations.Operation;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/hotel-rooms")
public class HotelRoomController {

    private final HotelRoomService hotelRoomService;

    @GetMapping("/filter")
    public List<HotelRoomResponse> getFilteredRooms(
            @RequestParam(required = false) CategoryRoom category,
            @RequestParam(required = false) Boolean available) {
        List<HotelRoomEntity> rooms = hotelRoomService.findRoomsByFilters(category, available);
        return rooms.stream().map(HotelRoomResponse::toDtoWithoutReservations).toList();
    }

    @GetMapping("/filter/pageable")
    @Operation(summary = "Get filtered rooms with pagination (same pagination params as reservations)")
    public Page<HotelRoomResponse> getFilteredRoomsPageable(
            @RequestParam(required = false) CategoryRoom category,
            @RequestParam(required = false) Boolean available,
            @ParameterObject Pageable pageable) {
        return hotelRoomService
                .findRoomsByFiltersPageable(category, available, pageable)
                .map(HotelRoomResponse::toDtoWithoutReservations);
    }

    @GetMapping("/available")
    public List<HotelRoomResponse> getAllAvailableRooms() {
        List<HotelRoomEntity> rooms = hotelRoomService.findAllAvailableRooms();
        return rooms.stream().map(HotelRoomResponse::toDtoWithoutReservations).toList();
    }

    @GetMapping("/category/{category}")
    public List<HotelRoomResponse> getAllRoomsByCategory(@PathVariable CategoryRoom category) {
        List<HotelRoomEntity> rooms = hotelRoomService.findAllRoomsByCategory(category);
        return rooms.stream().map(HotelRoomResponse::toDtoWithoutReservations).toList();
    }

    @GetMapping("/{id}")
    public HotelRoomResponse getRoomById(@PathVariable UUID id) {
        HotelRoomEntity room = hotelRoomService.findById(id);
        return HotelRoomResponse.toDto(room);
    }

    @GetMapping("/roomNumber/{roomNumber}")
    public HotelRoomResponse getRoomByRoomNumber(@PathVariable int roomNumber) {
        HotelRoomEntity room = hotelRoomService.findByRoomNumber(roomNumber);
        return HotelRoomResponse.toDto(room);
    }

    @PostMapping
    public HotelRoomResponse createRoom(@RequestBody CreateRoomRequest createRoomRequest) {
        HotelRoomEntity createdRoomEntity = hotelRoomService.save(createRoomRequest);
        return HotelRoomResponse.toDto(createdRoomEntity);
    }

    @PutMapping("/{id}")
    public HotelRoomResponse updateRoom(@PathVariable UUID id, @RequestBody CreateRoomRequest createRoomRequest) {
        HotelRoomEntity updatedRoom = hotelRoomService.update(id, createRoomRequest);
        return HotelRoomResponse.toDto(updatedRoom);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(com.ede.est_hotel_pro.entity.account.Role).ADMIN)")
    public void deleteHotelRoom(@PathVariable UUID id) {
        hotelRoomService.deleteHotelRoomById(id);
    }

    // (format: yyyy-MM-dd)
    @GetMapping("/available-on-date")
    @Operation(summary = "Get all rooms available on a specific date. Reservation Page -> Just to show number")
    public List<HotelRoomResponse> getAvailableRoomsOnDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Convert LocalDate to Instant at midnight UTC
        Instant dateInstant = date.atStartOfDay(ZoneId.of("UTC")).toInstant();

        List<HotelRoomEntity> availableRooms = hotelRoomService.findAvailableRoomsOnDate(dateInstant);
        return availableRooms.stream().map(HotelRoomResponse::toDtoWithoutReservations).toList();
    }

    // (format: yyyy-MM-dd)
    @GetMapping("/available-between-dates")
    @Operation(summary = "Get all rooms available between two dates. Reservation Creation page")
    public List<HotelRoomResponse> getAvailableRoomsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Convert LocalDate to Instant at midnight UTC
        Instant startDateInstant = startDate.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant endDateInstant = endDate.atStartOfDay(ZoneId.of("UTC")).toInstant();

        List<HotelRoomEntity> availableRooms = hotelRoomService.findAvailableRoomsBetweenDates(startDateInstant, endDateInstant);
        return availableRooms.stream().map(HotelRoomResponse::toDtoWithoutReservations).toList();
    }
}