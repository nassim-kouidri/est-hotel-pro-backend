package com.ede.est_hotel_pro.dto.create;

import com.ede.est_hotel_pro.entity.hotelroom.CategoryRoom;

public record CreateRoomRequest(
        int roomNumber,
        int price,
        CategoryRoom category,
        String state) {
}
