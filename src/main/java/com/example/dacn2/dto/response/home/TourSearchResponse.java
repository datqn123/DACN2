package com.example.dacn2.dto.response.home;

import lombok.*;
import java.util.List;

/**
 * DTO cho kết quả tìm kiếm tour với phân trang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourSearchResponse {

    private List<TourCardResponse> tours; // Danh sách tour

    private int currentPage; // Trang hiện tại (bắt đầu từ 0)
    private int totalPages; // Tổng số trang
    private long totalElements; // Tổng số kết quả
    private int pageSize; // Số phần tử mỗi trang
    private boolean hasNext; // Còn trang tiếp theo?
    private boolean hasPrevious; // Có trang trước?
}
