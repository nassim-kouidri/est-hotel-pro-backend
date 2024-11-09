package com.ede.est_hotel_pro.entity.hotelroom;

import com.ede.est_hotel_pro.entity.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hotel_room")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class HotelRoomEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private int roomNumber;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryRoom category;

    @Nullable
    private String state;

    @Column(nullable = false)
    private boolean available = true;

    @Column
    private String imageUrl;

}
