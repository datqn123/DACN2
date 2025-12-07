// src/main/java/com/example/dacn2/dto/response/voucher/HotelSummary.java
package com.example.dacn2.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelSummary {
    private Long id;
    private String name;
    private String address;
    private String image;
    private Integer starRating;
    private Double averageRating;
    private Double lowestPrice;
}