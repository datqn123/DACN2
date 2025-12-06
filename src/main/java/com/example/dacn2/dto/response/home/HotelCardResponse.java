package com.example.dacn2.dto.response.home;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelCardResponse {

    private Long id;

    private String name; // Tên khách sạn

    private String address; // Địa chỉ

    private Integer starRating; // Số sao (1-5)

    private String locationName; // Tên địa điểm (VD: "Đà Nẵng")

    private String thumbnail; // Ảnh đại diện (lấy từ images[0])

    private Double minPrice; // Giá phòng thấp nhất/đêm

    private String hotelType; // HOTEL, RESORT, etc.
}