package com.ede.est_hotel_pro.entity.hotelroom;

import com.ede.est_hotel_pro.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hotel_room")
@Getter
@Setter
@RequiredArgsConstructor
public class HotelRoomEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private int numberRoom;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryRoom category;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private boolean available = true;
}
