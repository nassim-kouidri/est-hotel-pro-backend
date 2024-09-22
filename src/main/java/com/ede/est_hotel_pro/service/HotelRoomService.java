package com.ede.est_hotel_pro.service;

import com.ede.est_hotel_pro.dto.create.CreateRoomRequest;
import com.ede.est_hotel_pro.entity.hotelroom.CategoryRoom;
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

    public List<HotelRoomEntity> findAllRooms() {
        return hotelRoomRepository.findAll();
    }

    public HotelRoomEntity findById(UUID id) {
        return hotelRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Hotel room with id '%s' not found", id)));
    }

    public HotelRoomEntity findByRoomNumber(int roomNumber) {
        return hotelRoomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Hotel room with number '%s' not found", roomNumber)));

    }

    public List<HotelRoomEntity> findAllAvailableRooms() {
        return findAllRooms().stream().filter(HotelRoomEntity::isAvailable).toList();
    }

    public List<HotelRoomEntity> findAllRoomsByCategory(CategoryRoom categoryRoom) {
        return hotelRoomRepository.findAllByCategory(categoryRoom);
    }

    public HotelRoomEntity save(CreateRoomRequest roomRequest) {
        HotelRoomEntity hotelRoomEntity = new HotelRoomEntity().toBuilder()
                .roomNumber(roomRequest.roomNumber())
                .price(roomRequest.price())
                .category(roomRequest.category())
                .state(roomRequest.state())
                .build();

        return hotelRoomRepository.save(hotelRoomEntity);
    }

    public void deleteHotelRoomById(UUID id) {
        HotelRoomEntity roomEntity = findById(id);
        hotelRoomRepository.delete(roomEntity);
    }
}
