package com.example.dacn2.repository.hotel;

import com.example.dacn2.dto.request.hotel.HotelFilterRequest;
import com.example.dacn2.entity.booking.Booking;
import com.example.dacn2.entity.booking.BookingStatus;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.Room;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification class để xây dựng dynamic query cho Hotel
 * Hỗ trợ filter linh hoạt với các điều kiện optional
 * Bao gồm Real-time Availability check
 */
public class HotelSpecification {

    public static Specification<Hotel> withFilters(HotelFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter theo location slug
            if (filter.getLocationSlug() != null && !filter.getLocationSlug().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("location").get("slug")),
                        filter.getLocationSlug().toLowerCase()));
            }

            // Filter theo khoảng giá phòng (minPrice)
            if (filter.getMinPrice() != null && query != null) {
                Subquery<Double> minRoomPriceSubquery = query.subquery(Double.class);
                var roomRoot = minRoomPriceSubquery.from(Room.class);
                minRoomPriceSubquery.select(criteriaBuilder.min(roomRoot.get("price")))
                        .where(criteriaBuilder.equal(roomRoot.get("hotel"), root));

                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        minRoomPriceSubquery, filter.getMinPrice()));
            }

            // Filter theo khoảng giá phòng (maxPrice)
            if (filter.getMaxPrice() != null && query != null) {
                Subquery<Double> minRoomPriceSubquery = query.subquery(Double.class);
                var roomRoot = minRoomPriceSubquery.from(Room.class);
                minRoomPriceSubquery.select(criteriaBuilder.min(roomRoot.get("price")))
                        .where(criteriaBuilder.equal(roomRoot.get("hotel"), root));

                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        minRoomPriceSubquery, filter.getMaxPrice()));
            }

            // Filter theo đánh giá sao tối thiểu
            if (filter.getMinStarRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("starRating"), filter.getMinStarRating()));
            }

            // Filter theo đánh giá sao tối đa
            if (filter.getMaxStarRating() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("starRating"), filter.getMaxStarRating()));
            }

            // Filter theo loại hình (HotelType)
            if (filter.getHotelType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("type"), filter.getHotelType()));
            }

            // Chỉ hiện hotel có ít nhất 1 phòng còn trống trong khoảng ngày
            if (filter.getCheckInDate() != null && filter.getCheckOutDate() != null && query != null) {
                LocalDateTime checkIn = filter.getCheckInDate().atStartOfDay();
                LocalDateTime checkOut = filter.getCheckOutDate().atStartOfDay();

                // Subquery: Đếm số booking overlap với khoảng ngày yêu cầu
                Subquery<Long> bookedCountSubquery = query.subquery(Long.class);
                Root<Booking> bookingRoot = bookedCountSubquery.from(Booking.class);

                bookedCountSubquery.select(criteriaBuilder.count(bookingRoot))
                        .where(
                                // Join ngược từ Booking -> Room -> Hotel
                                criteriaBuilder.equal(bookingRoot.get("room").get("hotel"), root),
                                // Chỉ đếm phòng available
                                criteriaBuilder.isTrue(bookingRoot.get("room").get("isAvailable")),
                                // Dùng Enum thay vì String
                                criteriaBuilder.notEqual(bookingRoot.get("status"), BookingStatus.CANCELLED),
                                criteriaBuilder.lessThan(bookingRoot.get("checkInDate"), checkOut),
                                criteriaBuilder.greaterThan(bookingRoot.get("checkOutDate"), checkIn));

                // Subquery: Tổng số phòng available
                Subquery<Long> totalRoomsSubquery = query.subquery(Long.class);
                Root<Room> roomRoot2 = totalRoomsSubquery.from(Room.class);
                totalRoomsSubquery.select(criteriaBuilder.coalesce(
                        criteriaBuilder.sum(roomRoot2.get("quantity")), 0L))
                        .where(
                                criteriaBuilder.equal(roomRoot2.get("hotel"), root),
                                criteriaBuilder.isTrue(roomRoot2.get("isAvailable")));

                // Điều kiện: Tổng phòng > Số đã đặt
                predicates.add(criteriaBuilder.greaterThan(totalRoomsSubquery, bookedCountSubquery));
            }

            // Loại bỏ duplicate
            if (query != null) {
                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
