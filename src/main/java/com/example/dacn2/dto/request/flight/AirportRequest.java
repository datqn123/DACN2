package com.example.dacn2.dto.request.flight;

import lombok.Data;

@Data
public class AirportRequest {
    private String name; // VD: "Sân bay quốc tế Tân Sơn Nhất"
    private String code; // VD: "SGN", "HAN", "DAD"
    private Long locationId; // ID địa điểm (thành phố)
}
