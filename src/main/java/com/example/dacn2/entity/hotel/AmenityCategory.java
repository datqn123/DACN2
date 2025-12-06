package com.example.dacn2.entity.hotel;

import com.example.dacn2.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "amenity_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmenityCategory extends BaseEntity {

    @Column(nullable = false)
    private String name; // VD: "Ẩm thực", "Dịch vụ khách sạn"

    // Một nhóm có nhiều tiện nghi con
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Amenity> amenities;
}