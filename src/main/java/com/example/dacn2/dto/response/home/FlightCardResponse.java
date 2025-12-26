package com.example.dacn2.dto.response.home;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightCardResponse {
    private Long id;
    private String flightNumber; // VN123
    private String airlineLogo; // Logo hãng
    private String airlineName; // Vietnam Airlines

    // Thông tin hành trình
    private String departureTime; // 08:00
    private String arrivalTime; // 09:30
    private String duration; // 1h 30m

    private String fromLocation; // TP.HCM
    private String toLocation; // Hà Nội

    private Double minPrice; // Giá rẻ nhất (của ghế Economy)
    private String image;
}