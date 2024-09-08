package com.ede.est_hotel_pro.repository;

import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HotelRoomRepository extends JpaRepository<HotelRoomEntity, UUID> {
}
