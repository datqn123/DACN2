package com.example.dacn2.entity.voucher;

import com.example.dacn2.entity.BaseEntity;
import com.example.dacn2.entity.voucher.VoucherScope;
import com.example.dacn2.entity.voucher.DiscountType;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.tour.Tour;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voucher extends BaseEntity {

    // 1. Thông tin hiển thị (UI)
    @Column(nullable = false, unique = true)
    private String code; // UI: "TRIPSTAY30", "TRIPFL25"

    @Column(nullable = false)
    private String name; // UI: "Ưu đãi khách sạn dành riêng cho bạn"

    @Column(columnDefinition = "TEXT")
    private String description; // UI: "Áp dụng cho khách hàng đã đăng ký..."

    private String image; // UI: Ảnh nền voucher

    // 2. Logic giảm giá (Discount Logic)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // PERCENTAGE hoặc FIXED_AMOUNT

    private Double discountValue; // VD: 30 (nếu là %) hoặc 100000 (nếu là tiền)

    private Double maxDiscountAmount; // QUAN TRỌNG: Giảm 30% nhưng tối đa bao nhiêu tiền? (VD: Max 500k)

    private Double minOrderValue; // Đơn hàng tối thiểu để dùng voucher (VD: 1 triệu)

    // 3. Thời gian áp dụng (Timeframe)
    @Column(nullable = false)
    private LocalDateTime startDate; // UI: 20/11/2025

    @Column(nullable = false)
    private LocalDateTime endDate;   // UI: 15/12/2025

    // 4. Giới hạn sử dụng (Usage Limits)
    private Integer usageLimit; // Tổng số lượng voucher phát hành (VD: 1000 cái)

    private Integer usageCount = 0; // UI: "672 lượt" đã sử dụng. (Tăng lên khi có người đặt)

    private Integer userLimit; // Một người được dùng tối đa mấy lần? (VD: 1 lần)

    // 5. Phạm vi áp dụng (Scope)
    @Enumerated(EnumType.STRING)
    private VoucherScope scope; // HOTEL_ONLY, TOUR_ONLY...

    private Boolean isActive = true; // Admin có thể tắt voucher khẩn cấp

    // --- MỐI QUAN HỆ (Advanced) ---
    // UI: "Chỉ áp dụng các khách sạn trong danh sách"

    // Nếu voucher này chỉ dành riêng cho một số Khách sạn cụ thể
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "voucher_applied_hotels",
            joinColumns = @JoinColumn(name = "voucher_id"),
            inverseJoinColumns = @JoinColumn(name = "hotel_id")
    )
    private Set<Hotel> appliedHotels;

    // Nếu voucher này chỉ dành riêng cho một số Tour cụ thể
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "voucher_applied_tours",
            joinColumns = @JoinColumn(name = "voucher_id"),
            inverseJoinColumns = @JoinColumn(name = "tour_id")
    )
    private Set<Tour> appliedTours;
}