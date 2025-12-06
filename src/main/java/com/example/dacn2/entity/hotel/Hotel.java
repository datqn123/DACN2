package com.example.dacn2.entity.hotel;

import com.example.dacn2.entity.BaseEntity;
import com.example.dacn2.entity.Location;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hotel extends BaseEntity { // Kế thừa id, createdAt, updatedAt từ BaseEntity

        @Column(nullable = false)
        private String name; // Tên khách sạn

        @Column(nullable = false)
        private String address; // Địa chỉ cụ thể (Số nhà, đường...)

        @Column(columnDefinition = "TEXT")
        private String description; // Mô tả dài

        private Integer starRating; // 1 đến 5 sao

        @Enumerated(EnumType.STRING)
        private HotelType type; // HOTEL, RESORT...

        // Giờ Check-in/Check-out (Mặc định 14:00 và 12:00)
        private String checkInTime = "14:00";
        private String checkOutTime = "12:00";

        // Thông tin liên hệ
        private String contactPhone;
        private String contactEmail;

        // ================ RECOMMENDATION SYSTEM FIELDS ================

        // --- 1. ĐÁNH GIÁ VÀ REVIEW ---

        // Điểm đánh giá trung bình (0.0 - 10.0)
        @Column(name = "average_rating")
        private Double averageRating;

        // Tổng số lượt đánh giá
        @Column(name = "total_reviews")
        private Integer totalReviews = 0;

        // Điểm đánh giá chi tiết (0.0 - 10.0)
        @Column(name = "cleanliness_score")
        private Double cleanlinessScore; // Độ sạch sẽ

        @Column(name = "comfort_score")
        private Double comfortScore; // Tiện nghi

        @Column(name = "location_score")
        private Double locationScore; // Vị trí

        @Column(name = "facilities_score")
        private Double facilitiesScore; // Cơ sở vật chất

        @Column(name = "staff_score")
        private Double staffScore; // Nhân viên phục vụ

        // Giá thấp nhất cho một đêm (VND)
        @Column(name = "price_per_night_from")
        private Double pricePerNightFrom;

        // Mức giá (BUDGET, MODERATE, UPSCALE, LUXURY)
        @Enumerated(EnumType.STRING)
        @Column(name = "price_range")
        private PriceRange priceRange;

        // --- 4. THÔNG TIN BỔ SUNG ---
        // Phong cách thiết kế (MODERN, TRADITIONAL, BOUTIQUE, etc.)
        @Enumerated(EnumType.STRING)
        @Column(name = "design_style")
        private DesignStyle designStyle;
        // 1. Thuộc về địa điểm nào? (Đà Nẵng, Hà Nội...)
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "location_id", nullable = false)
        private Location location;

        // 3. Có những tiện nghi gì? (Quan hệ Nhiều - Nhiều)
        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "hotels_amenities", joinColumns = @JoinColumn(name = "hotel_id"), inverseJoinColumns = @JoinColumn(name = "amenity_id"))
        private Set<Amenity> amenities;

        // 4. Danh sách ảnh
        @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<HotelImage> images;

        // 5. Danh sách phòng
        @OneToMany(mappedBy = "hotel")
        private List<Room> rooms;

        // 6. Danh sách review (cho recommendation system)
        @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
        private List<HotelReview> reviews;

        @ElementCollection(targetClass = HotelView.class, fetch = FetchType.EAGER)
        @CollectionTable(name = "hotel_views", joinColumns = @JoinColumn(name = "hotel_id"))
        @Enumerated(EnumType.STRING) // Lưu dưới dạng chữ (OCEAN_VIEW) thay vì số (0, 1)
        @Column(name = "view_type")
        private Set<HotelView> views;
}