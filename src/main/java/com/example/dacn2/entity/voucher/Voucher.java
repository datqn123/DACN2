package com.example.dacn2.entity.voucher;

import com.example.dacn2.entity.BaseEntity;
import com.example.dacn2.entity.Location;
import com.example.dacn2.entity.flight.Flight; // Nhớ import đúng package Flight
import com.example.dacn2.entity.hotel.Hotel; // Nhớ import đúng package Hotel
import com.example.dacn2.entity.tour.Tour; // Nhớ import đúng package Tour
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

        // --- 1. THÔNG TIN CƠ BẢN ---
        @Column(nullable = false, unique = true)
        private String code; // Mã nhập (VD: TRAVEL2025)

        @Column(nullable = false)
        private String name; // Tên chương trình (VD: "Chào hè rực rỡ")

        @Column(columnDefinition = "TEXT")
        private String description;

        private String image; // Ảnh banner voucher

        // --- 2. LOGIC GIẢM GIÁ ---
        @Enumerated(EnumType.STRING)
        private DiscountType discountType; // % hay Tiền

        private Double discountValue; // Giá trị (VD: 10 hoặc 50000)

        private Double maxDiscountAmount; // Giảm tối đa (VD: 500k) - Quan trọng khi giảm %

        private Double minOrderValue; // Đơn tối thiểu để dùng (VD: 1 triệu)

        // --- 3. THỜI HẠN & GIỚI HẠN ---
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        private Integer usageLimit; // Tổng số lượng mã (VD: 1000)
        private Integer usageCount = 0; // Đã dùng bao nhiêu?

        private Integer userLimit; // Mỗi người dùng được mấy lần? (VD: 1)

        private Boolean isActive = true;

        // --- 4. PHẠM VI ÁP DỤNG (SCOPE) ---
        @Enumerated(EnumType.STRING)
        private VoucherScope scope;

        @Column(name = "for_new_users_only")
        private Boolean forNewUsersOnly = false;

        // Danh sách Khách sạn áp dụng (Nếu scope = HOTEL_ONLY)
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "voucher_applied_hotels", joinColumns = @JoinColumn(name = "voucher_id"), inverseJoinColumns = @JoinColumn(name = "hotel_id"))
        private Set<Hotel> appliedHotels;

        // Danh sách Tour áp dụng (Nếu scope = TOUR_ONLY)
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "voucher_applied_tours", joinColumns = @JoinColumn(name = "voucher_id"), inverseJoinColumns = @JoinColumn(name = "tour_id"))
        private Set<Tour> appliedTours;

        // Danh sách Chuyến bay áp dụng (Nếu scope = FLIGHT_ONLY)
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "voucher_applied_flights", joinColumns = @JoinColumn(name = "voucher_id"), inverseJoinColumns = @JoinColumn(name = "flight_id"))
        private Set<Flight> appliedFlights;

        // Logic: Nếu list này không rỗng, dịch vụ phải nằm trong các địa điểm này mới
        // được dùng voucher
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "voucher_applied_locations", joinColumns = @JoinColumn(name = "voucher_id"), inverseJoinColumns = @JoinColumn(name = "location_id"))
        private Set<Location> appliedLocations;
}