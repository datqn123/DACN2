package com.example.dacn2.entity.hotel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "hotel_images")
@Data
public class HotelImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl; // Link ảnh trên Cloudinary/S3

    private String caption; // Chú thích ảnh (VD: "Sảnh chính", "Góc nhìn từ trên cao")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    @JsonIgnore
    private Hotel hotel;
}