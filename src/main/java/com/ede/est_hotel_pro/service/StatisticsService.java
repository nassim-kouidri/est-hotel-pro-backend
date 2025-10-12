package com.ede.est_hotel_pro.service;

import com.ede.est_hotel_pro.dto.out.statistics.*;
import com.ede.est_hotel_pro.entity.hotelroom.HotelRoomEntity;
import com.ede.est_hotel_pro.entity.reservation.PaymentStatus;
import com.ede.est_hotel_pro.entity.reservation.ReservationEntity;
import com.ede.est_hotel_pro.repository.HotelRoomRepository;
import com.ede.est_hotel_pro.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private static final ZoneId UTC = ZoneId.of("UTC");

    private final ReservationRepository reservationRepository;
    private final HotelRoomRepository hotelRoomRepository;

    public OverviewStatsResponse getOverview(LocalDate startDate, LocalDate endDate) {
        Instant start = toUtcStart(startDate);
        Instant endExclusive = toUtcStart(endDate); // [start, end)

        long days = Math.max(0, ChronoUnit.DAYS.between(startDate, endDate));
        long totalRooms = hotelRoomRepository.count();
        long roomCapacityNights = totalRooms * days;

        List<ReservationEntity> overlapping = reservationRepository.findOverlapping(start, endExclusive);

        long totalReservations = overlapping.size();
        long occupiedRoomNights = 0L;
        double revenue = 0.0;
        double contractedRevenue = 0.0;
        long contractedCount = 0L;
        long sumNightsForAvg = 0L;

        for (ReservationEntity r : overlapping) {
            LocalDate rStart = r.getStartDate().atZone(UTC).toLocalDate();
            LocalDate rEnd = r.getEndDate().atZone(UTC).toLocalDate();

            long totalNights = Math.max(0, ChronoUnit.DAYS.between(rStart, rEnd));
            LocalDate s = rStart.isAfter(startDate) ? rStart : startDate;
            LocalDate e = rEnd.isBefore(endDate) ? rEnd : endDate;
            long nightsInRange = Math.max(0, ChronoUnit.DAYS.between(s, e));

            occupiedRoomNights += nightsInRange;
            sumNightsForAvg += nightsInRange;

            if (totalNights > 0 && nightsInRange > 0) {
                double perNight = ((double) r.getPricePaid()) / totalNights;
                revenue += perNight * nightsInRange;
                if (r.isContracted()) {
                    contractedRevenue += perNight * nightsInRange;
                }
            }
            if (r.isContracted()) {
                contractedCount++;
            }
        }

        double occupancyRate = roomCapacityNights > 0 ? (double) occupiedRoomNights / roomCapacityNights : 0.0;
        double adr = occupiedRoomNights > 0 ? revenue / occupiedRoomNights : 0.0;
        double revpar = roomCapacityNights > 0 ? revenue / roomCapacityNights : 0.0;
        double avgLengthOfStay = totalReservations > 0 ? ((double) sumNightsForAvg) / totalReservations : 0.0;
        double contractedShare = totalReservations > 0 ? ((double) contractedCount) / totalReservations : 0.0;

        return new OverviewStatsResponse(
                totalReservations,
                round2(revenue),
                occupiedRoomNights,
                roomCapacityNights,
                round4(occupancyRate),
                round2(adr),
                round2(revpar),
                round2(avgLengthOfStay),
                round4(contractedShare),
                round2(contractedRevenue)
        );
    }

    public List<DailyOccupancyPoint> getDailyOccupancy(LocalDate startDate, LocalDate endDate) {
        long totalRooms = hotelRoomRepository.count();
        List<Object[]> rows = reservationRepository.dailyOccupiedRoomNights(startDate, endDate);
        return rows.stream()
                .map(row -> {
                    LocalDate d = toLocalDate(row[0]);
                    long occ = toLong(row[1]);
                    double rate = totalRooms > 0 ? (double) occ / totalRooms : 0.0;
                    return new DailyOccupancyPoint(d, occ, round4(rate));
                })
                .collect(Collectors.toList());
    }

    public List<DailyReservationPoint> getDailyReservations(LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Long> counts = new LinkedHashMap<>();
        Map<LocalDate, Double> revenues = new LinkedHashMap<>();

        List<Object[]> countRows = reservationRepository.dailyReservationCounts(startDate, endDate);
        for (Object[] row : countRows) {
            counts.put(toLocalDate(row[0]), toLong(row[1]));
        }
        List<Object[]> revenueRows = reservationRepository.dailyRevenue(startDate, endDate);
        for (Object[] row : revenueRows) {
            revenues.put(toLocalDate(row[0]), toDouble(row[1]));
        }

        // Ensure continuous date series
        LocalDate d = startDate;
        List<DailyReservationPoint> result = new ArrayList<>();
        while (d.isBefore(endDate)) {
            long c = counts.getOrDefault(d, 0L);
            double rev = round2(revenues.getOrDefault(d, 0.0));
            result.add(new DailyReservationPoint(d, c, rev));
            d = d.plusDays(1);
        }
        return result;
    }

    public List<CategorySlice> getCategorySlice(LocalDate startDate, LocalDate endDate) {
        Instant start = toUtcStart(startDate);
        Instant endExclusive = toUtcStart(endDate);
        List<ReservationEntity> overlapping = reservationRepository.findOverlapping(start, endExclusive);

        Map<String, long[]> acc = new HashMap<>(); // category -> [roomNights, reservations]
        for (ReservationEntity r : overlapping) {
            HotelRoomEntity room = r.getHotelRoom();
            String category = room != null && room.getCategory() != null ? room.getCategory().name() : "UNKNOWN";

            LocalDate rStart = r.getStartDate().atZone(UTC).toLocalDate();
            LocalDate rEnd = r.getEndDate().atZone(UTC).toLocalDate();
            LocalDate s = rStart.isAfter(startDate) ? rStart : startDate;
            LocalDate e = rEnd.isBefore(endDate) ? rEnd : endDate;
            long nightsInRange = Math.max(0, ChronoUnit.DAYS.between(s, e));

            long[] vals = acc.computeIfAbsent(category, k -> new long[]{0L, 0L});
            vals[0] += nightsInRange;
            vals[1] += 1;
        }
        return acc.entrySet().stream()
                .map(e -> new CategorySlice(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .sorted(Comparator.comparing(CategorySlice::roomNights).reversed())
                .collect(Collectors.toList());
    }

    public List<PaymentStatusSlice> getPaymentStatusSlice(LocalDate startDate, LocalDate endDate) {
        Instant start = toUtcStart(startDate);
        Instant endExclusive = toUtcStart(endDate);
        List<Object[]> rows = reservationRepository.paymentStatusAggregate(start, endExclusive);
        return rows.stream()
                .map(r -> new PaymentStatusSlice(
                        (PaymentStatus) r[0],
                        toLong(r[1]),
                        round2(toDouble(r[2]))
                ))
                .sorted(Comparator.comparing(PaymentStatusSlice::count).reversed())
                .collect(Collectors.toList());
    }

    public List<CompanyTop> getTopCompanies(LocalDate startDate, LocalDate endDate, int limit) {
        Instant start = toUtcStart(startDate);
        Instant endExclusive = toUtcStart(endDate);
        List<ReservationEntity> overlapping = reservationRepository.findOverlapping(start, endExclusive);

        Map<String, CompanyAgg> map = new HashMap<>();
        for (ReservationEntity r : overlapping) {
            String name = normalizeCompany(r.getCompanyName());
            if (!r.isContracted() || name.isEmpty()) continue;

            LocalDate rStart = r.getStartDate().atZone(UTC).toLocalDate();
            LocalDate rEnd = r.getEndDate().atZone(UTC).toLocalDate();
            long totalNights = Math.max(0, ChronoUnit.DAYS.between(rStart, rEnd));
            LocalDate s = rStart.isAfter(startDate) ? rStart : startDate;
            LocalDate e = rEnd.isBefore(endDate) ? rEnd : endDate;
            long nightsInRange = Math.max(0, ChronoUnit.DAYS.between(s, e));

            CompanyAgg agg = map.computeIfAbsent(name, k -> new CompanyAgg());
            agg.reservations++;
            agg.roomNights += nightsInRange;
            if (totalNights > 0 && nightsInRange > 0) {
                double perNight = ((double) r.getPricePaid()) / totalNights;
                agg.revenue += perNight * nightsInRange;
            }
        }

        return map.entrySet().stream()
                .map(e -> new CompanyTop(e.getKey(), e.getValue().reservations, round2(e.getValue().revenue), e.getValue().roomNights))
                .sorted(Comparator.comparing(CompanyTop::revenue).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // --- Helpers ---
    private static Instant toUtcStart(LocalDate d) {
        return d.atStartOfDay(UTC).toInstant();
    }

    private static LocalDate toLocalDate(Object dbDate) {
        if (dbDate instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        if (dbDate instanceof java.time.LocalDate ld) {
            return ld;
        }
        // Fallback: try parse string
        return LocalDate.parse(dbDate.toString());
    }

    private static long toLong(Object n) {
        return n == null ? 0L : ((Number) n).longValue();
    }

    private static double toDouble(Object n) {
        return n == null ? 0.0 : ((Number) n).doubleValue();
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private static double round4(double v) {
        return Math.round(v * 10000.0) / 10000.0;
    }

    private static String normalizeCompany(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.isEmpty()) return "";
        return t.toUpperCase(Locale.ROOT);
    }

    private static final class CompanyAgg {
        long reservations = 0;
        long roomNights = 0;
        double revenue = 0.0;
    }
}
