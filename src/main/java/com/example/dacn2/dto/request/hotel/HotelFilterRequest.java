package com.example.dacn2.dto.request.hotel;

import com.example.dacn2.entity.hotel.HotelType;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO chứa các tham số filter cho tìm kiếm khách sạn
 * Tất cả các field đều optional, có thể kết hợp với nhau
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelFilterRequest {

    private String locationSlug; // Slug địa điểm (VD: "da-nang", "ho-chi-minh")

    private Double minPrice; // Giá tối thiểu
    private Double maxPrice; // Giá tối đa

    private Integer minStarRating; // Đánh giá sao tối thiểu (1-5)
    private Integer maxStarRating; // Đánh giá sao tối đa (1-5)

    private HotelType hotelType; // Loại hình: HOTEL, RESORT, HOMESTAY, VILLA, APARTMENT

    private String sortByPrice; // Sắp xếp theo giá: "ASC" (thấp -> cao), "DESC" (cao -> thấp)

    private Integer page; // Số trang (bắt đầu từ 0, mặc định = 0)

    // === REAL-TIME AVAILABILITY ===
    private LocalDate checkInDate; // Ngày nhận phòng
    private LocalDate checkOutDate; // Ngày trả phòng
}
