package com.example.dacn2.dto.response.home;

import lombok.*;

import java.util.List;

/**
 * DTO cho kết quả phân trang tìm kiếm khách sạn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelSearchResponse {

    private List<HotelCardResponse> hotels; // Danh sách khách sạn

    private int currentPage; // Trang hiện tại (bắt đầu từ 0)
    private int totalPages; // Tổng số trang
    private long totalElements; // Tổng số kết quả
    private int pageSize; // Số lượng mỗi trang (mặc định 20)
    private boolean hasNext; // Có trang tiếp theo không
    private boolean hasPrevious; // Có trang trước không
}
