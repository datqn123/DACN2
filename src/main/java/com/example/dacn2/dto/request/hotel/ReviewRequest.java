package com.example.dacn2.dto.request.hotel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotNull
    private Long hotelId;

    private String comment;

    // Các điểm số (1.0 đến 10.0)
    @Min(1)
    @Max(10)
    private Double cleanliness;
    @Min(1)
    @Max(10)
    private Double comfort;
    @Min(1)
    @Max(10)
    private Double location;
    @Min(1)
    @Max(10)
    private Double staff;
    @Min(1)
    @Max(10)
    private Double facilities;
}