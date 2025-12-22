package com.example.dacn2.entity.tour;

import com.example.dacn2.entity.BaseEntity;
import com.example.dacn2.entity.Location;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tour extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    private String duration; // "2N1Đ"
    private Double priceAdult; // Giá người lớn
    private Double priceChild; // Giá trẻ em
    private Double price; // Giá hiện tại (sau giảm)
    private Double originalPrice; // Giá gốc (hiển thị gạch ngang)
    private String thumbnail;
    private String maxPeople; // "4-6 người"

    @Column(columnDefinition = "TEXT")
    private String description;

    private String transportation; // Máy bay, Tàu hỏa...

    // Điểm nổi bật (lưu dạng JSON string)
    // VD: ["Cano cao tốc đời mới", "Phục vụ ăn sáng đặc sản", "Homestay view biển"]
    @Column(columnDefinition = "TEXT")
    private String highlights;

    // Bao gồm (lưu dạng JSON string)
    // VD: ["Xe đưa đón Đà Nẵng/Hội An", "Cano cao tốc khứ hồi", "Vé tham quan Cù
    // Lao Chàm"]
    @Column(columnDefinition = "TEXT")
    private String includes;

    // Không bao gồm (lưu dạng JSON string)
    // VD: ["Chi phí cá nhân", "Tiền tip cho HDV"]
    @Column(columnDefinition = "TEXT")
    private String excludes;

    // 1. Nơi Khởi Hành (Start) - VD: Khởi hành từ TP.HCM
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "start_location_id", nullable = false)
    private Location startLocation;

    // 2. Nơi Đến (Destination) - VD: Đi chơi ở Đà Nẵng
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_id", nullable = false)
    private Location destination;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourImage> images;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourSchedule> schedules;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourItinerary> itineraries;
}