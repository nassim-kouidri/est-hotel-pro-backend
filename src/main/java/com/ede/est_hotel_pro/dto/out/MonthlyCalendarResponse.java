package com.ede.est_hotel_pro.dto.out;

import java.util.Map;

/**
 * DTO for returning reservation counts for each day in a month.
 * This is used for displaying a monthly calendar with reservation counts.
 */
public record MonthlyCalendarResponse(
        int year,
        int month,
        Map<Integer, Integer> dailyReservationCounts // Map of day of month to reservation count
) {
    /**
     * Factory method to create a MonthlyCalendarResponse from a year, month, and daily reservation counts.
     *
     * @param year                   The year
     * @param month                  The month (1-12)
     * @param dailyReservationCounts Map of day of month to reservation count
     * @return A new MonthlyCalendarResponse
     */
    public static MonthlyCalendarResponse create(int year, int month, Map<Integer, Integer> dailyReservationCounts) {
        return new MonthlyCalendarResponse(year, month, dailyReservationCounts);
    }
}