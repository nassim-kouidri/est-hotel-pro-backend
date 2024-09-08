package com.ede.est_hotel_pro.service;

import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;
import com.ede.est_hotel_pro.repository.HotelRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HotelRoomService {

    private final HotelRoomRepository hotelRoomRepository;

    public List<HotelRoomEntity> getAllRooms() {
        return hotelRoomRepository.findAll();
    }

    public HotelRoomEntity getRoomById(UUID id) {
        return hotelRoomRepository.findById(id).orElse(null);
    }

    public HotelRoomEntity saveRoom(HotelRoomEntity room) {
        return hotelRoomRepository.save(room);
    }
}
