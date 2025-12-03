package com.example.dacn2.dto.request.tour;
import lombok.Data;
import java.util.List;

@Data
public class TourRequest {
    private String title;
    private String slug;
    private String duration; // "3N2Đ"
    private Double priceAdult;
    private Double priceChild;
    private String description;
    private String transportation; // "Máy bay"

    private Long startLocationId; // ID Nơi đi
    private Long destinationId;   // ID Nơi đến

    // Danh sách con
    private List<TourScheduleRequest> schedules;
    private List<TourItineraryRequest> itineraries;

    // Link ảnh copy (nếu có)
    private List<String> imageUrls;
}