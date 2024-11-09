package com.ede.est_hotel_pro.dto.out;

import com.ede.est_hotel_pro.entity.hotelroom.CategoryRoom;
import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;

import java.util.UUID;

public record HotelRoomResponse(
        UUID id,
        int roomNumber,
        int price,
        CategoryRoom category,
        String state,
        boolean available,
        String imageUrl) {

    public static HotelRoomResponse toDto(HotelRoomEntity roomEntity) {
        return new HotelRoomResponse(
                roomEntity.getId(),
                roomEntity.getRoomNumber(),
                roomEntity.getPrice(),
                roomEntity.getCategory(),
                roomEntity.getState(),
                roomEntity.isAvailable(),
                roomEntity.getImageUrl()
        );
    }
}