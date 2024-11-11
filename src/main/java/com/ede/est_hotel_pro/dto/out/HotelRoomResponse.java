package com.ede.est_hotel_pro.dto.out;

import com.ede.est_hotel_pro.entity.hotelroom.CategoryRoom;
import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;

import java.util.List;
import java.util.UUID;

public record HotelRoomResponse(
        UUID id,
        int roomNumber,
        int price,
        CategoryRoom category,
        String state,
        boolean available,
        String imageUrl,
        List<ReservationResponse> reservations) {

    public static HotelRoomResponse toDto(HotelRoomEntity roomEntity) {
        List<ReservationResponse> reservationResponses = roomEntity.getReservations().stream().map(ReservationResponse::toDtoWithoutRoom).toList();
        return new HotelRoomResponse(
                roomEntity.getId(),
                roomEntity.getRoomNumber(),
                roomEntity.getPrice(),
                roomEntity.getCategory(),
                roomEntity.getState(),
                roomEntity.isAvailable(),
                roomEntity.getImageUrl(),
                reservationResponses
        );
    }

    public static HotelRoomResponse toDtoWithoutReservations(HotelRoomEntity roomEntity) {
        return new HotelRoomResponse(
                roomEntity.getId(),
                roomEntity.getRoomNumber(),
                roomEntity.getPrice(),
                roomEntity.getCategory(),
                roomEntity.getState(),
                roomEntity.isAvailable(),
                roomEntity.getImageUrl(),
                null
        );
    }
}