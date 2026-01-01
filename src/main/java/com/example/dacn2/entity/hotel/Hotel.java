package com.example.dacn2.entity.hotel;

import com.example.dacn2.entity.BaseEntity;
import com.example.dacn2.entity.Location;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hotels", indexes = {
                @Index(name = "idx_hotel_location", columnList = "location_id")
})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hotel extends BaseEntity { 

        @Column(nullable = false)
        private String name; 

        @Column(nullable = false)
        private String address; 

        @Column(columnDefinition = "TEXT")
        private String description; 

        private Integer starRating; 

        @Enumerated(EnumType.STRING)
        private HotelType type; 

        private String checkInTime = "14:00";
        private String checkOutTime = "12:00";

        private String contactPhone;
        private String contactEmail;

        private Double averageRating;

        @Column(name = "total_reviews")
        private Integer totalReviews = 0;

        @Column(name = "cleanliness_score")
        private Double cleanlinessScore; 

        @Column(name = "comfort_score")
        private Double comfortScore; 

        @Column(name = "location_score")
        private Double locationScore; 

        @Column(name = "facilities_score")
        private Double facilitiesScore; 

        @Column(name = "staff_score")
        private Double staffScore; 

        @Column(name = "price_per_night_from")
        private Double pricePerNightFrom;

        @Enumerated(EnumType.STRING)
        @Column(name = "price_range")
        private PriceRange priceRange;

        @Enumerated(EnumType.STRING)
        @Column(name = "design_style")
        private DesignStyle designStyle;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "location_id", nullable = false)
        private Location location;

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "hotels_amenities", joinColumns = @JoinColumn(name = "hotel_id"), inverseJoinColumns = @JoinColumn(name = "amenity_id"))
        private Set<Amenity> amenities;

        @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<HotelImage> images;

        @OneToMany(mappedBy = "hotel", cascade = CascadeType.REMOVE)
        private List<Room> rooms;

        @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
        private List<HotelReview> reviews;

        @ElementCollection(targetClass = HotelView.class, fetch = FetchType.EAGER)
        @CollectionTable(name = "hotel_views", joinColumns = @JoinColumn(name = "hotel_id"))
        @Enumerated(EnumType.STRING) 
        @Column(name = "view_type")
        private Set<HotelView> views;
}