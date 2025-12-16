package com.example.dacn2.dto.response.home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationCardResponse {
    private Long id;
    private String name; // VD: "Hàn Quốc"
    private String slug; // VD: "han-quoc" (để khi click vào sẽ chuyển trang)
    private String thumbnail; // Ảnh đại diện
}