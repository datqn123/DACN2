package com.example.dacn2.entity.hotel;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "amenities")
@Data
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // VD: "Hồ bơi", "Wifi miễn phí"

    private String icon; // Mã icon (font-awesome) hoặc link ảnh icon
}