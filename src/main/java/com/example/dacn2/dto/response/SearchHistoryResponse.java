package com.example.dacn2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO trả về lịch sử tìm kiếm cho client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryResponse {
    private Long id;
    private String keyword;
    private String searchType;
    private Integer resultCount;

    // Thông tin recommendation
    private Long locationId;
    private String locationName;
    private Double minPrice;
    private Double maxPrice;
    private Integer starRating;
    private String hotelType;

    private LocalDateTime createdAt;
}
