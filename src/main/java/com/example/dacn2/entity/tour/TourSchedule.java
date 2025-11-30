package com.example.dacn2.entity.tour;

import com.example.dacn2.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tour_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourSchedule extends BaseEntity {

    @Column(nullable = false)
    private LocalDate startDate; // Ngày đi (VD: 20/11/2025)

    private LocalDate endDate;   // Ngày về

    private Integer availableSeats; // Số chỗ còn nhận (VD: 20)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    @JsonIgnore
    private Tour tour;
}