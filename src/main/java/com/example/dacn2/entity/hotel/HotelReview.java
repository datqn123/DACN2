package com.example.dacn2.entity.hotel;

import com.example.dacn2.entity.BaseEntity;
import com.example.dacn2.entity.User.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hotel_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelReview extends BaseEntity {

    // 1. Điểm số chi tiết (Thang 1-10)
    private Double cleanlinessRating; // Độ sạch sẽ
    private Double comfortRating; // Tiện nghi
    private Double locationRating; // Vị trí
    private Double staffRating; // Nhân viên
    private Double facilitiesRating; // Cơ sở vật chất

    // 2. Điểm trung bình của review này (Tự tính)
    private Double averageRating;

    // 3. Nội dung bình luận
    @Column(columnDefinition = "TEXT")
    private String comment;

    // 4. Quan hệ
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private Account user; // Ai đánh giá?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonIgnore
    private Hotel hotel; // Đánh giá khách sạn nào?
}