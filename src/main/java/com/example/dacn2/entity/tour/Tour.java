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

    private String duration;
    private Double priceAdult;
    private Double priceChild;
    private Double price;
    private String thumbnail;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String transportation; // Máy bay, Tàu hỏa...

    // --- SỬA ĐỔI Ở ĐÂY ---

    // 1. Nơi Khởi Hành (Start) - VD: Khởi hành từ TP.HCM
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "start_location_id", nullable = false)
    private Location startLocation;

    // 2. Nơi Đến (Destination) - VD: Đi chơi ở Đà Nẵng
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_id", nullable = false)
    private Location destination;

    // ---------------------

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<TourImage> images;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<TourSchedule> schedules;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<TourItinerary> itineraries;
}