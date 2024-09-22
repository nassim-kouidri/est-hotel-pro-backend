package com.ede.est_hotel_pro.entity.reservation;

import com.ede.est_hotel_pro.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;


import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID idRoom;

    @Column
    @Type(JsonBinaryType.class)
    private UserSnapshot userReservation;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant endDate;

    @Column(nullable = false)
    private int numberOfChildren;

    @Column(nullable = false)
    private int numberOfAdults;

    @Column(nullable = false)
    private int pricePaid;

    private String claim;

    private long review;
}
