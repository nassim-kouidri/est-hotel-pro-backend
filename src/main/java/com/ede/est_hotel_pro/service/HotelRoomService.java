package com.ede.est_hotel_pro.service;

import com.ede.est_hotel_pro.dto.create.CreateRoomRequest;
import com.ede.est_hotel_pro.entity.BaseEntity;
import com.ede.est_hotel_pro.entity.hotelroom.CategoryRoom;
import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;
import com.ede.est_hotel_pro.repository.HotelRoomRepository;
import com.ede.est_hotel_pro.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelRoomService {

    private final String ULR_DEFAULT_IMAGE = "https://i.postimg.cc/xd7vWR7w/ede-chambre-default.jpg";
    private final HotelRoomRepository hotelRoomRepository;
    private final ReservationRepository reservationRepository;

    public List<HotelRoomEntity> findAllRooms() {
        return hotelRoomRepository.findAll();
    }

    public List<HotelRoomEntity> findAllAvailableRooms() {
        return findAllRooms().stream().filter(HotelRoomEntity::isAvailable).toList();
    }

    public List<HotelRoomEntity> findAllRoomsByCategory(CategoryRoom categoryRoom) {
        return hotelRoomRepository.findAllByCategory(categoryRoom);
    }

    public List<HotelRoomEntity> findRoomsByFilters(CategoryRoom category, Boolean available) {
        return hotelRoomRepository.findAllByCategoryAndAvailable(category, available);
    }

    public HotelRoomEntity findById(UUID id) {
        return hotelRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Hotel room with id '%s' not found", id)));
    }

    public HotelRoomEntity findByRoomNumber(int roomNumber) {
        return hotelRoomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Hotel room with number '%s' not found", roomNumber)));

    }

    @Transactional
    public HotelRoomEntity save(CreateRoomRequest roomRequest) {
        checkCreateAndUpdateRoom(roomRequest);
        if (hotelRoomRepository.findByRoomNumber(roomRequest.roomNumber()).isPresent()) {
            throw new IllegalArgumentException(String.format("Room number '%s' already exists", roomRequest.roomNumber()));
        }
        HotelRoomEntity hotelRoomEntity = new HotelRoomEntity().toBuilder()
                .roomNumber(roomRequest.roomNumber())
                .price(roomRequest.price())
                .category(roomRequest.category())
                .state(roomRequest.state())
                .imageUrl(getImageUrl(roomRequest))
                .build();

        return hotelRoomRepository.save(hotelRoomEntity);
    }

    @Transactional
    public HotelRoomEntity update(UUID id, CreateRoomRequest roomRequest) {
        checkCreateAndUpdateRoom(roomRequest);
        HotelRoomEntity existingRoom = findById(id);
        existingRoom.setPrice(roomRequest.price());
        existingRoom.setState(roomRequest.state());
        existingRoom.setImageUrl(roomRequest.imageUrl());
        return hotelRoomRepository.save(existingRoom);
    }

    @Transactional
    public void updateAvailability(HotelRoomEntity roomEntity) {
        HotelRoomEntity existingRoom = findById(roomEntity.getId());
        existingRoom.setAvailable(roomEntity.isAvailable());
        hotelRoomRepository.save(existingRoom);
    }

    @Transactional
    public void deleteHotelRoomById(UUID id) {
        HotelRoomEntity roomEntity = findById(id);
        if (!roomEntity.getReservations().isEmpty()) {
            throw new IllegalStateException(String.format("Cannot delete room with id '%s' because it has existing reservations", id));
        }
        hotelRoomRepository.delete(roomEntity);
    }

    private void checkCreateAndUpdateRoom(CreateRoomRequest roomRequest) {
        if (roomRequest.price() < 0) {
            throw new IllegalArgumentException(String.format("Room price '%s' is invalid", roomRequest.price()));
        }
    }

    private String getImageUrl(CreateRoomRequest roomRequest) {
        return StringUtils.isEmpty(roomRequest.imageUrl()) ? ULR_DEFAULT_IMAGE : roomRequest.imageUrl();
    }

    public List<HotelRoomEntity> findAvailableRoomsOnDate(Instant date) {
        List<HotelRoomEntity> allRooms = findAllRooms();

        return allRooms.stream()
                .filter(room -> isRoomAvailableOnDate(room.getId(), date))
                .collect(Collectors.toList());
    }

    public List<HotelRoomEntity> findAvailableRoomsBetweenDates(Instant startDate, Instant endDate) {
        List<HotelRoomEntity> allRooms = findAllRooms();

        return allRooms.stream()
                .filter(room -> isRoomAvailableBetweenDates(room.getId(), startDate, endDate))
                .collect(Collectors.toList());
    }

    private boolean isRoomAvailableOnDate(UUID roomId, Instant date) {
        // Create a time range for the entire day
        Instant startOfDay = date.minusSeconds(date.getEpochSecond() % 86400);
        Instant endOfDay = startOfDay.plusSeconds(86400);

        List<UUID> overlappingReservations = reservationRepository
                .findAllByHotelRoom_IdAndStartDateLessThanAndEndDateGreaterThan(roomId, endOfDay, startOfDay)
                .stream()
                .map(BaseEntity::getId)
                .toList();

        return overlappingReservations.isEmpty();
    }

    private boolean isRoomAvailableBetweenDates(UUID roomId, Instant startDate, Instant endDate) {
        // Ensure startDate is before endDate
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        List<UUID> overlappingReservations = reservationRepository
                .findAllByHotelRoom_IdAndStartDateLessThanAndEndDateGreaterThan(roomId, endDate, startDate)
                .stream()
                .map(BaseEntity::getId)
                .toList();

        return overlappingReservations.isEmpty();
    }
}