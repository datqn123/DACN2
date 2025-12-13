package com.example.dacn2.repository.tour;

import com.example.dacn2.dto.request.tour.TourFilterRequest;
import com.example.dacn2.entity.tour.Tour;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification class để xây dựng dynamic query cho Tour
 * Hỗ trợ filter linh hoạt với các điều kiện optional
 */
public class TourSpecification {

    public static Specification<Tour> withFilters(TourFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter theo destination ID
            if (filter.getDestinationId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("destination").get("id"),
                        filter.getDestinationId()));
            }

            // Filter theo khoảng giá (minPrice)
            if (filter.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"), filter.getMinPrice()));
            }

            // Filter theo khoảng giá (maxPrice)
            if (filter.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"), filter.getMaxPrice()));
            }

            // Filter theo thời gian (durationCategory)
            // Duration format: "2N1Đ", "3N2Đ", "5N4Đ"...
            if (filter.getDurationCategory() != null && !filter.getDurationCategory().isEmpty()) {
                String category = filter.getDurationCategory();
                switch (category) {
                    case "1-2" -> {
                        // 1-2 ngày: 1N hoặc 2N
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(root.get("duration"), "1N%"),
                                criteriaBuilder.like(root.get("duration"), "2N%")));
                    }
                    case "3-4" -> {
                        // 3-4 ngày
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(root.get("duration"), "3N%"),
                                criteriaBuilder.like(root.get("duration"), "4N%")));
                    }
                    case "5-7" -> {
                        // 5-7 ngày
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(root.get("duration"), "5N%"),
                                criteriaBuilder.like(root.get("duration"), "6N%"),
                                criteriaBuilder.like(root.get("duration"), "7N%")));
                    }
                    case "7+" -> {
                        // Trên 7 ngày (8N, 9N, 10N...)
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(root.get("duration"), "8N%"),
                                criteriaBuilder.like(root.get("duration"), "9N%"),
                                criteriaBuilder.like(root.get("duration"), "1%N%") // 10N+
                        ));
                    }
                }
            }

            // Loại bỏ duplicate
            if (query != null) {
                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
