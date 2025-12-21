package com.example.dacn2.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalResponse {
    private Long totalHotel;
    private Long totalBooking;
    private Long totalTour;
    private Double totalRevenue;
}
