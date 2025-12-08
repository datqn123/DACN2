package com.example.dacn2.repository.hotel;

import com.example.dacn2.dto.request.hotel.HotelFilterRequest;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.Room;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification class để xây dựng dynamic query cho Hotel
 * Hỗ trợ filter linh hoạt với các điều kiện optional
 * Filter theo giá phòng (Room.price) thay vì pricePerNightFrom
 */
public class HotelSpecification {

    /**
     * Tạo Specification từ HotelFilterRequest
     * Các điều kiện sẽ được AND với nhau
     */
    public static Specification<Hotel> withFilters(HotelFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter theo location slug
            if (filter.getLocationSlug() != null && !filter.getLocationSlug().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("location").get("slug")),
                        filter.getLocationSlug().toLowerCase()));
            }

            // Filter theo khoảng giá phòng (minPrice) - Tìm hotel có giá phòng thấp nhất >=
            // minPrice
            if (filter.getMinPrice() != null && query != null) {
                Subquery<Double> minRoomPriceSubquery = query.subquery(Double.class);
                var roomRoot = minRoomPriceSubquery.from(Room.class);
                minRoomPriceSubquery.select(criteriaBuilder.min(roomRoot.get("price")))
                        .where(criteriaBuilder.equal(roomRoot.get("hotel"), root));

                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        minRoomPriceSubquery, filter.getMinPrice()));
            }

            // Filter theo khoảng giá phòng (maxPrice) - Tìm hotel có giá phòng thấp nhất <=
            // maxPrice
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

            // Loại bỏ duplicate
            if (query != null) {
                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
