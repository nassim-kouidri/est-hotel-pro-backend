package com.ede.est_hotel_pro.service;

import com.ede.est_hotel_pro.dto.create.CreateRoomRequest;
import com.ede.est_hotel_pro.entity.hotelroom.CategoryRoom;
import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;
import com.ede.est_hotel_pro.repository.HotelRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HotelRoomService {

    private final String ULR_DEFAULT_IMAGE = "https://img.freepik.com/photos-gratuite/surface-abstraite-textures-mur-pierre-beton-blanc_74190-8189.jpg";
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

    @Transactional
    public HotelRoomEntity save(CreateRoomRequest roomRequest) {
        checkCreateAndUpdateRoom(roomRequest);
        if (hotelRoomRepository.findByRoomNumber(roomRequest.roomNumber()).isPresent()) {
            throw new IllegalArgumentException(String.format("Room number '%s' already exists", roomRequest.roomNumber()));
        }
        HotelRoomEntity hotelRoomEntity = new HotelRoomEntity().toBuilder()
                .roomNumber(roomRequest.roomNumber())
                .price(roomRequest.price())
                .category(roomRequest.category())
                .state(roomRequest.state())
                .imageUrl(getImageUrl(roomRequest))
                .build();

        return hotelRoomRepository.save(hotelRoomEntity);
    }

    @Transactional
    public HotelRoomEntity update(UUID id, CreateRoomRequest roomRequest) {
        checkCreateAndUpdateRoom(roomRequest);
        HotelRoomEntity existingRoom = findById(id);
        existingRoom.setPrice(roomRequest.price());
        existingRoom.setState(roomRequest.state());
        existingRoom.setImageUrl(roomRequest.imageUrl());
        return hotelRoomRepository.save(existingRoom);
    }

    @Transactional
    public void updateAvailability(HotelRoomEntity roomEntity) {
        HotelRoomEntity existingRoom = findById(roomEntity.getId());
        existingRoom.setAvailable(roomEntity.isAvailable());
        hotelRoomRepository.save(existingRoom);
    }

    @Transactional
    public void deleteHotelRoomById(UUID id) {
        HotelRoomEntity roomEntity = findById(id);
        if (!roomEntity.getReservations().isEmpty()) {
            throw new IllegalStateException(String.format("Cannot delete room with id '%s' because it has existing reservations", id));
        }
        hotelRoomRepository.delete(roomEntity);
    }

    private void checkCreateAndUpdateRoom(CreateRoomRequest roomRequest) {
        if (roomRequest.price() < 0) {
            throw new IllegalArgumentException(String.format("Room price '%s' is invalid", roomRequest.price()));
        }
    }

    private String getImageUrl(CreateRoomRequest roomRequest) {
        return StringUtils.isEmpty(roomRequest.imageUrl()) ? ULR_DEFAULT_IMAGE : roomRequest.imageUrl();
    }
}