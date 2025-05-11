package com.ede.est_hotel_pro.dto.out;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for returning detailed information about reservations for a specific date.
 * This is used when a user clicks on a specific day in the calendar.
 */
public record DailyReservationsResponse(
        LocalDate date,
        int totalReservations,
        List<ReservationResponse> reservations
) {
    /**
     * Factory method to create a DailyReservationsResponse from a date and list of reservations.
     *
     * @param date         The date
     * @param reservations List of reservations for the date
     * @return A new DailyReservationsResponse
     */
    public static DailyReservationsResponse create(LocalDate date, List<ReservationResponse> reservations) {
        return new DailyReservationsResponse(date, reservations.size(), reservations);
    }
}