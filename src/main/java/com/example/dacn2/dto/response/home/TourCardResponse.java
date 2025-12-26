package com.example.dacn2.dto.response.home;

import java.io.Serializable;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourCardResponse implements Serializable {

    private Long id;

    private String title; // Tên tour

    private String slug; // URL-friendly

    private String duration; // Thời gian (VD: "3 ngày 2 đêm")

    private String startLocationName; // Nơi khởi hành (VD: "TP.HCM")

    private String destinationName; // Nơi đến (VD: "Đà Nẵng")

    private String thumbnail; // Ảnh đại diện

    private Double price; // Giá tour (có thể dùng priceAdult hoặc field price)

    private String transportation; // Phương tiện (Máy bay, tàu hỏa...)
}