package com.example.dacn2.dto.request.tour;

import lombok.*;

/**
 * DTO chứa các tham số filter cho tìm kiếm tour
 * Tất cả các field đều optional, có thể kết hợp với nhau
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourFilterRequest {

    private Long destinationId; // ID địa điểm đến (VD: 1, 2, 3)

    private Double minPrice; // Giá tối thiểu
    private Double maxPrice; // Giá tối đa

    private String durationCategory; // Loại thời gian: "1-2", "3-4", "5-7", "7+"

    private String sortByPrice; // Sắp xếp theo giá: "ASC" (thấp -> cao), "DESC" (cao -> thấp)

    private Integer page; // Số trang (bắt đầu từ 0, mặc định = 0)
}
