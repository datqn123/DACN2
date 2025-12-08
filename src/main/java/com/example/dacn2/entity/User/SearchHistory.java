package com.example.dacn2.entity.User;

import com.example.dacn2.entity.Location;
import com.example.dacn2.entity.hotel.HotelType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity lưu lịch sử tìm kiếm của user
 * Dùng cho gợi ý tìm kiếm và recommendation system
 */
@Entity
@Table(name = "search_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // Từ khóa tìm kiếm (VD: "Đà Nẵng", "resort biển")
    @Column(name = "keyword", length = 255)
    private String keyword;

    // Loại tìm kiếm
    @Enumerated(EnumType.STRING)
    @Column(name = "search_type", length = 20)
    private SearchType searchType;

    // Số kết quả tìm được
    @Column(name = "result_count")
    private Integer resultCount;

    // === Thông tin mở rộng cho Recommendation ===

    // Địa điểm đã tìm -> gợi ý hotel cùng khu vực
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    // Khoảng giá đã filter -> gợi ý hotel cùng tầm giá
    @Column(name = "min_price")
    private Double minPrice;

    @Column(name = "max_price")
    private Double maxPrice;

    // Số sao đã filter -> gợi ý hotel cùng hạng sao
    @Column(name = "star_rating")
    private Integer starRating;

    // Loại khách sạn đã filter -> Resort, Villa, Homestay...
    @Enumerated(EnumType.STRING)
    @Column(name = "hotel_type", length = 20)
    private HotelType hotelType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
