package com.example.dacn2.dto.request.location;

import com.example.dacn2.entity.enums.LocationType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LocationRequest {
    @NotBlank(message = "Tên địa điểm không được để trống")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    private String slug;

    private String description;

    private String thumbnail; // Link ảnh

    private LocationType type; // Enum: COUNTRY, PROVINCE...

    private Long parentId; // ID của địa điểm cha (Nếu có)
}