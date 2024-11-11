package com.ede.est_hotel_pro.entity.hotelroom;

import com.ede.est_hotel_pro.entity.BaseEntity;
import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @Column
    private String state;

    @Column(nullable = false)
    private boolean available = true;

    @Column
    private String imageUrl;

    @OneToMany(mappedBy = "hotelRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationEntity> reservations = new ArrayList<>();

}
