package com.example.dacn2.dto.response.home;

import com.example.dacn2.entity.enums.LocationType;
import lombok.AllArgsConstructor;
import lombok.Data;

// DTO này được dùng trong câu Query (JPQL)
@Data
@AllArgsConstructor
public class LocationSearchResult {
    private Long id;
    private String name;
    private String parentName; // Tên Quốc gia/Tỉnh (VD: Việt Nam)
    private LocationType type; // VD: "Thành Phố", "Tỉnh"
    private Long hotelCount; // Số lượng khách sạn tại địa điểm này

    // Getter cho tên đầy đủ (VD: Đà Lạt, Tỉnh Lâm Đồng, Việt Nam)
    public String getDisplayName() {
        if (parentName == null || parentName.isEmpty()) {
            return name;
        }
        return name + ", " + parentName;
    }
}