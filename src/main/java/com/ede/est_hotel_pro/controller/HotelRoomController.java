package com.ede.est_hotel_pro.controller;

import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;
import com.ede.est_hotel_pro.service.HotelRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<HotelRoomEntity>> getAllRooms() {
        List<HotelRoomEntity> rooms = hotelRoomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelRoomEntity> getRoomById(@PathVariable UUID id) {
        HotelRoomEntity room = hotelRoomService.getRoomById(id);
        if (room != null) {
            return ResponseEntity.ok(room);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<HotelRoomEntity> createRoom(@RequestBody HotelRoomEntity hotelRoomEntity) {
        HotelRoomEntity createdRoom = hotelRoomService.saveRoom(hotelRoomEntity);
        return ResponseEntity.ok(createdRoom);
    }

}