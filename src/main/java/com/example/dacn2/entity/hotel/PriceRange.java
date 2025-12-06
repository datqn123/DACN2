package com.example.dacn2.entity.hotel;

/**
 * Enum định nghĩa các mức giá cho khách sạn
 * Sử dụng cho recommendation system để lọc theo tầm giá
 */
public enum PriceRange {
    BUDGET, // Dưới 500k/đêm - Phù hợp với du khách tiết kiệm
    MODERATE, // 500k - 1.5M/đêm - Tầm trung, phổ biến
    UPSCALE, // 1.5M - 3M/đêm - Cao cấp
    LUXURY // Trên 3M/đêm - Sang trọng, dịch vụ 5 sao
}
