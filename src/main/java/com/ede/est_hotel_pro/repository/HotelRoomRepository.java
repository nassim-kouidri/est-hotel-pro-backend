package com.ede.est_hotel_pro.repository;

import com.ede.est_hotel_pro.entity.hotelroom.CategoryRoom;
import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;
import jdk.jfr.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HotelRoomRepository extends JpaRepository<HotelRoomEntity, UUID> {

    Optional<HotelRoomEntity> findByRoomNumber(int roomNumber);

    List<HotelRoomEntity> findAllByCategory(CategoryRoom category);

    @Query("SELECT r FROM HotelRoomEntity r WHERE (:category IS NULL OR r.category = :category) AND (:available IS NULL OR r.available = :available) ORDER BY r.roomNumber ASC")
    List<HotelRoomEntity> findAllByCategoryAndAvailable(@Param("category") CategoryRoom category, @Param("available") Boolean available);
}
