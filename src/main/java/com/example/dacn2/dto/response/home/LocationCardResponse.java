package com.example.dacn2.dto.response.home;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationCardResponse {
    private Long id;
    private String name;       // VD: "Hàn Quốc"
    private String slug;       // VD: "han-quoc" (để khi click vào sẽ chuyển trang)
    private String thumbnail;  // Ảnh đại diện
}