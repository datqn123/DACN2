package com.example.dacn2.entity.hotel;

import com.example.dacn2.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "amenities")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Amenity extends BaseEntity {

    @Column(nullable = false)
    private String name; // VD: "Wifi miễn phí", "Nhà hàng"

    private String icon; // Lưu tên class icon (VD: "fa-wifi") hoặc URL ảnh icon

    // Đánh dấu đây có phải tiện ích chính không (để hiện lên đầu trang như ảnh 2)
    @Column(name = "is_prominent")
    private Boolean isProminent = false;

    // Thuộc nhóm nào? (VD: Thuộc nhóm "Ẩm thực")
    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore // Tránh vòng lặp khi load Category
    private AmenityCategory category;
}