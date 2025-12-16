package com.example.dacn2.entity.hotel;

import com.example.dacn2.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms", indexes = {
        @Index(name = "idx_room_hotel", columnList = "hotel_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room extends BaseEntity {

    @Column(nullable = false)
    private String name; // VD: "Deluxe King Ocean View"

    private Double price; // Giá 1 đêm (VD: 2.500.000)

    private Integer capacity; // Số người tối đa (VD: 2)

    private Integer quantity; // Tổng số lượng phòng loại này (VD: 10 phòng)

    private Double area; // Diện tích (m2) - VD: 45.5

    private Boolean isAvailable = true; // Còn mở bán không?

    // Quan hệ: Nhiều phòng thuộc về 1 Khách sạn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonIgnore // Ngắt vòng lặp JSON (Khi xem phòng không cần in lại toàn bộ thông tin khách
                // sạn)
    private Hotel hotel;
}