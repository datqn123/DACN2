package com.example.dacn2.dto.request.flight;

import lombok.Data;

@Data
public class AirlineRequest {
    private String name; // VD: "Vietnam Airlines"
    private String code; // VD: "VN", "VJ"
    private String logoUrl; // URL logo hãng bay
}
