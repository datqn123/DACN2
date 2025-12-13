package com.example.dacn2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FligthCardResponse {
    private Long id;

    // Thông tin hãng bay
    private String airlineName; // "Vietnam Airlines"
    private String airlineLogo; // URL logo hãng bay

    // Thông tin sân bay
    private String departureCode; // "SGN"
    private String departureCity; // "TP HCM"
    private String arrivalCode; // "DAD"
    private String arrivalCity; // "Đà Nẵng"

    // Thời gian
    private String departureTime; // "06:00"
    private String arrivalTime; // "07:30"
    private String duration; // "1h 30m"
    private String flightDate; // "28/12/2025"

    // Giá vé (hiển thị giá Economy thấp nhất)
    private Double originalPrice; // 1437500 (giá gốc)

    // Thông tin bổ sung
    private String flightNumber; // "VN123"
}
