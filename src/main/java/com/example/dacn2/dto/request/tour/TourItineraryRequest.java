package com.example.dacn2.dto.request.tour;
import lombok.Data;

@Data
public class TourItineraryRequest {
    private Integer dayNumber; // Ngày 1, Ngày 2
    private String title;      // "Đón sân bay"
    private String description;
}