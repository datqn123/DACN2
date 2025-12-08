package com.example.dacn2.dto.response.home;

import com.example.dacn2.entity.voucher.DiscountType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherCardResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String image;
    private DiscountType discountType;
    private Double discountValue;
    private Double maxDiscountAmount;
    private Boolean isActive;
}
