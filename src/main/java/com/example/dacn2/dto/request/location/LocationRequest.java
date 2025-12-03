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

    private String thumbnail;

    private LocationType type;

    private Long parentId;
}