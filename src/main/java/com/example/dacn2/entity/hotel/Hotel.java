package com.example.dacn2.entity.hotel;

import com.example.dacn2.entity.BaseEntity;
import com.example.dacn2.entity.HotelImage;
import com.example.dacn2.entity.Location;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.hotel.HotelType;
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

    // --- CÁC MỐI QUAN HỆ ---

    // 1. Thuộc về địa điểm nào? (Đà Nẵng, Hà Nội...)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    // 2. Ai là chủ khách sạn này? (Account có role HOTEL_OWNER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Account owner;

    // 3. Có những tiện nghi gì? (Quan hệ Nhiều - Nhiều)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "hotels_amenities",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities;

    // 4. Danh sách ảnh
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelImage> images;

     @OneToMany(mappedBy = "hotel")
     private List<Room> rooms;
}