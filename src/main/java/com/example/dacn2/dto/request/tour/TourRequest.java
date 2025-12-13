package com.example.dacn2.dto.request.tour;

import lombok.Data;
import java.util.List;

@Data
public class TourRequest {
    private String title;
    private String slug;
    private String duration; // "2N1Đ"
    private Double priceAdult;
    private Double priceChild;
    private Double price; // Giá hiện tại
    private Double originalPrice; // Giá gốc (để hiển thị gạch)
    private String maxPeople; // "4-6 người"
    private String description;
    private String transportation; // "Máy bay"

    private Long startLocationId; // ID Nơi đi
    private Long destinationId; // ID Nơi đến

    // Điểm nổi bật
    private List<String> highlights;
    // Bao gồm
    private List<String> includes;
    // Không bao gồm
    private List<String> excludes;

    // Danh sách con
    private List<TourScheduleRequest> schedules;
    private List<TourItineraryRequest> itineraries;

    // Link ảnh copy (nếu có)
    private List<String> imageUrls;
}