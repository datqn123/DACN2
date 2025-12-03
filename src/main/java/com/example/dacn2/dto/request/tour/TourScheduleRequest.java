package com.example.dacn2.dto.request.tour;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TourScheduleRequest {
    private LocalDate startDate; // 2025-12-20
    private LocalDate endDate;
    private Integer availableSeats;
}