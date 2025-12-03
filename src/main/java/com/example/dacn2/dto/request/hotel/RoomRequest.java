package com.example.dacn2.dto.request.hotel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRequest {
    @NotBlank(message = "Tên phòng không được để trống")
    private String name;

    @NotNull(message = "Giá phòng không được để trống")
    private Double price;

    private Integer capacity;
    private Integer quantity;
    private Double area;
    private Boolean isAvailable;

    @NotNull(message = "Phải chọn khách sạn")
    private Long hotelId; // Phòng này thuộc khách sạn nào?
}