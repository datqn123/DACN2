package com.example.dacn2.dto.request.hotel;

import com.example.dacn2.entity.hotel.HotelType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class HotelRequest {
    @NotBlank(message = "Tên khách sạn không được để trống")
    private String name;

    private String address;
    private String description;
    private Integer starRating;
    private HotelType type; // HOTEL, RESORT, VILLA...

    private String checkInTime;
    private String checkOutTime;

    private String contactPhone;
    private String contactEmail;

    private Long locationId; // Khách sạn ở đâu?

    private List<Long> amenityIds; // Danh sách ID các tiện nghi (VD: [1, 2, 5])

    private List<String> imageUrls; // Danh sách link ảnh (VD: ["link1.jpg", "link2.jpg"])
}