// src/main/java/com/example/dacn2/dto/response/voucher/HotelSummary.java
package com.example.dacn2.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DropdownLocationResponse {
    private Long id;
    private String name;
    private Long count;
    private String thumbnail;
}