package com.example.dacn2.entity.tour;

import com.example.dacn2.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tour_itineraries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourItinerary extends BaseEntity {

    private Integer dayNumber; // Ngày thứ mấy (1, 2, 3...)

    private String title; // Tiêu đề (VD: "Khám phá Bà Nà")

    @Column(columnDefinition = "TEXT")
    private String description; // Chi tiết hoạt động

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    @JsonIgnore
    private Tour tour;
}