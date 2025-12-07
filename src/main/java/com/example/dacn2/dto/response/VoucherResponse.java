// src/main/java/com/example/dacn2/dto/response/voucher/VoucherResponse.java
package com.example.dacn2.dto.response;

import com.example.dacn2.entity.voucher.DiscountType;
import com.example.dacn2.entity.voucher.VoucherScope;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String image;
    private DiscountType discountType;
    private Double discountValue;
    private Double maxDiscountAmount;
    private Double minOrderValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private Integer usageCount;
    private Integer userLimit;
    private Boolean isActive;
    private VoucherScope scope;
    private List<HotelSummary> appliedHotels;
}