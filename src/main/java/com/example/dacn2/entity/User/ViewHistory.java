package com.example.dacn2.entity.User;

import com.example.dacn2.entity.BaseEntity;
import com.example.dacn2.entity.Location;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.HotelType;
import com.example.dacn2.entity.hotel.PriceRange;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity lưu lịch sử xem chi tiết hotel của user
 * Dùng cho Recommendation System - Collaborative & Content-based Filtering
 * 
 * Thông tin thu thập:
 * 1. WHO: Ai đang xem? (account_id)
 * 2. WHAT: Xem khách sạn nào? (hotel_id + các đặc điểm snapshot)
 * 3. WHEN: Xem lúc nào? (viewedAt)
 * 4. HOW LONG: Xem bao lâu? (viewDuration)
 * 5. CONTEXT: Đến từ đâu, qua search hay homepage? (source, searchQuery)
 * 6. ACTION: Có hành động gì sau đó? (clicked, booked, favorited)
 */
@Entity
@Table(name = "view_histories", indexes = {
        @Index(name = "idx_view_account", columnList = "account_id"),
        @Index(name = "idx_view_hotel", columnList = "hotel_id"),
        @Index(name = "idx_view_time", columnList = "viewed_at"),
        @Index(name = "idx_view_account_hotel", columnList = "account_id, hotel_id"),
        @Index(name = "idx_view_location", columnList = "location_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewHistory extends BaseEntity {

    // ==================== CORE FIELDS ====================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account; // Ai xem?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel; // Xem khách sạn nào?

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt; // Xem lúc nào?

    // ==================== ENGAGEMENT METRICS ====================
    // Đo lường mức độ quan tâm của user

    /**
     * Thời gian xem trang (giây)
     * - < 10s: có thể là click nhầm
     * - 10-60s: xem lướt
     * - 60-180s: quan tâm
     * - > 180s: rất quan tâm
     */
    @Column(name = "view_duration_seconds")
    private Integer viewDurationSeconds;

    // ==================== USER ACTIONS ====================
    // Tracking các hành động sau khi xem

    @Column(name = "clicked_booking")
    @Builder.Default
    private Boolean clickedBooking = false; // Có click "Đặt phòng" không?

    @Column(name = "clicked_favorite")
    @Builder.Default
    private Boolean clickedFavorite = false; // Có thêm vào yêu thích không?

    @Column(name = "completed_payment")
    @Builder.Default
    private Boolean completedPayment = false; // Đã thanh toán thành công?

    @Column(name = "submitted_review")
    @Builder.Default
    private Boolean submittedReview = false; // Đã gửi đánh giá?

    // ==================== CONTEXT - Nguồn traffic ====================

    /**
     * Nguồn đến trang chi tiết
     * SEARCH: Từ kết quả tìm kiếm
     * HOMEPAGE: Từ gợi ý trang chủ
     * RECOMMENDATION: Từ gợi ý "Có thể bạn thích"
     * FAVORITE: Từ danh sách yêu thích
     * DIRECT: Truy cập trực tiếp
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "view_source", length = 30)
    private ViewSource viewSource;

    /**
     * Query tìm kiếm (nếu đến từ search)
     * VD: "resort đà nẵng 5 sao"
     */
    @Column(name = "search_query", length = 255)
    private String searchQuery;

    // ==================== HOTEL SNAPSHOT ====================
    // Lưu thông tin hotel TẠI THỜI ĐIỂM XEM (có thể thay đổi sau)
    // Giúp recommender biết user thích loại hotel nào

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location; // Địa điểm khách sạn

    @Column(name = "hotel_star_rating")
    private Integer hotelStarRating; // Số sao tại thời điểm xem

    @Enumerated(EnumType.STRING)
    @Column(name = "hotel_type", length = 20)
    private HotelType hotelType; // Loại khách sạn (HOTEL, RESORT, VILLA...)

    @Enumerated(EnumType.STRING)
    @Column(name = "hotel_price_range", length = 20)
    private PriceRange hotelPriceRange; // Mức giá (BUDGET, MODERATE, UPSCALE, LUXURY)

    @Column(name = "hotel_price_per_night")
    private Double hotelPricePerNight; // Giá thấp nhất tại thời điểm xem

    @Column(name = "hotel_average_rating")
    private Double hotelAverageRating; // Điểm đánh giá tại thời điểm xem

}
