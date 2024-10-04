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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
        return rooms.stream().map(HotelRoomResponse::toDto).toList();
    }

    @GetMapping("/available")
    public List<HotelRoomResponse> getAllAvailableRooms() {
        List<HotelRoomEntity> rooms = hotelRoomService.findAllAvailableRooms();
        return rooms.stream().map(HotelRoomResponse::toDto).toList();
    }

    @GetMapping("/category/{category}")
    public List<HotelRoomResponse> getAllRoomsByCategory(@PathVariable CategoryRoom category) {
        List<HotelRoomEntity> rooms = hotelRoomService.findAllRoomsByCategory(category);
        return rooms.stream().map(HotelRoomResponse::toDto).toList();
    }

    @GetMapping("/{id}")
    public HotelRoomResponse getRoomById(@PathVariable UUID id) {
        HotelRoomEntity room = hotelRoomService.findById(id);
        return HotelRoomResponse.toDto(room);
    }

    @GetMapping("/roomNumber/{roomNumber}")
    public HotelRoomResponse getRoomById(@PathVariable int roomNumber) {
        HotelRoomEntity room = hotelRoomService.findByRoomNumber(roomNumber);
        return HotelRoomResponse.toDto(room);
    }

    @PostMapping
    @PreAuthorize("hasRole(T(com.ede.est_hotel_pro.entity.account.Role).ADMIN)")
    public HotelRoomResponse createRoom(@RequestBody CreateRoomRequest createRoomRequest) {
        HotelRoomEntity createdRoomEntity = hotelRoomService.save(createRoomRequest);
        return HotelRoomResponse.toDto(createdRoomEntity);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(com.ede.est_hotel_pro.entity.account.Role).ADMIN)")
    public void deleteHotelRoom(@PathVariable UUID id) {
        hotelRoomService.deleteHotelRoomById(id);
    }

}