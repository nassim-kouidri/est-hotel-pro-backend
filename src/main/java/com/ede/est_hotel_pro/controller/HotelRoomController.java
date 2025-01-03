package com.ede.est_hotel_pro.controller;

import com.ede.est_hotel_pro.dto.create.CreateRoomRequest;
import com.ede.est_hotel_pro.dto.out.HotelRoomResponse;
import com.ede.est_hotel_pro.entity.hotelroom.CategoryRoom;
import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;
import com.ede.est_hotel_pro.service.HotelRoomService;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/hotel-rooms")
public class HotelRoomController {

    private final HotelRoomService hotelRoomService;

    @GetMapping
    public List<HotelRoomResponse> getAllRooms() {
        List<HotelRoomEntity> rooms = hotelRoomService.findAllRooms();
        return rooms.stream().map(HotelRoomResponse::toDtoWithoutReservations).toList();
    }

    @GetMapping("/filter")
    public List<HotelRoomResponse> getFilteredRooms(
            @RequestParam(required = false) CategoryRoom category,
            @RequestParam(required = false) Boolean available) {
        List<HotelRoomEntity> rooms = hotelRoomService.findRoomsByFilters(category, available);
        return rooms.stream().map(HotelRoomResponse::toDtoWithoutReservations).toList();
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

}