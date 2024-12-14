package com.ede.est_hotel_pro.entity.reservation;

import com.ede.est_hotel_pro.entity.BaseEntity;
import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;


import java.time.Instant;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ReservationEntity extends BaseEntity {

    @Column
    @Type(JsonBinaryType.class)
    private UserSnapshot userReservation;

    @ManyToOne(optional = false)
    private HotelRoomEntity hotelRoom;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant endDate;

    @Column(nullable = false)
    private int numberOfChildren;

    @Column(nullable = false)
    private int numberOfAdults;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private int pricePaid;

    @Column
    private String claim;

    @Column
    private long review;

    @Column(nullable = false)
    private boolean completed;

    public boolean isReservationFinished() {
        return this.status == ReservationStatus.ENDED;
    }

    public boolean isReservationInProgress() {
        return this.status == ReservationStatus.IN_PROGRESS;
    }
}
