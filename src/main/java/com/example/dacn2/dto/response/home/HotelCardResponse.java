package com.example.dacn2.dto.response.home;

import com.example.dacn2.entity.hotel.HotelType;
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

    private Boolean isFavorite; // true nếu user đã yêu thích hotel này

    /**
     * Constructor cho JPQL Projection
     * Chỉ lấy các cột cần thiết từ database
     */
    public HotelCardResponse(Long id, String name, String address, Integer starRating,
            String locationName, String thumbnail, Double minPrice, HotelType hotelType) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.starRating = starRating;
        this.locationName = locationName;
        this.thumbnail = thumbnail;
        this.minPrice = minPrice;
        this.hotelType = hotelType != null ? hotelType.name() : null;
        this.isFavorite = false; // Default, sẽ được set sau nếu cần
    }
}
