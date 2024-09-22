package com.ede.est_hotel_pro.service;

import com.ede.est_hotel_pro.dto.create.CreateReservationRequest;
import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import com.ede.est_hotel_pro.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<ReservationEntity> findAll() {
        return reservationRepository.findAll();
    }

    public ReservationEntity findById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("The reservation with id '%s' was not found", id)));

    }

    public ReservationEntity save(CreateReservationRequest request) {
        ReservationEntity reservation = new ReservationEntity().toBuilder()
                .startDate(request.startDate())
                .endDate(request.endDate())
                .userReservation(request.userSnapshot())
                .pricePaid(request.pricePaid())
                .numberOfChildren(request.numberOfChildren())
                .numberOfAdults(request.numberOfAdults())
                .claim(request.claim())
                .review(request.review())
                .build();

        return reservationRepository.save(reservation);
    }

    public void deleteById(UUID id) {
        ReservationEntity entity = findById(id);
        reservationRepository.delete(entity);
    }
}
