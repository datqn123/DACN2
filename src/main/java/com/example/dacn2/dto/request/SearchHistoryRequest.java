package com.example.dacn2.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO request để tạo lịch sử tìm kiếm
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryRequest {
    private String keyword; // Từ khóa tìm kiếm (bắt buộc)
    private String searchType; // HOTEL, FLIGHT, TOUR, LOCATION
    private Integer resultCount; // Số kết quả tìm được

    // Thông tin cho recommendation
    private Long locationId; // ID địa điểm đã tìm
    private Double minPrice; // Giá tối thiểu đã filter
    private Double maxPrice; // Giá tối đa đã filter
    private Integer starRating; // Số sao đã filter
    private String hotelType; // HOTEL, RESORT, HOMESTAY, VILLA, APARTMENT
}
