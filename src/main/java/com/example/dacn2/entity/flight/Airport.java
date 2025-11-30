package com.example.dacn2.entity.flight;

import com.example.dacn2.entity.BaseEntity;
import com.example.dacn2.entity.Location;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Airport extends BaseEntity {

    private String name; // VD: "Sân bay quốc tế Tân Sơn Nhất"

    private String code; // VD: "SGN", "HAN" (Hiển thị to trên vé)

    // Sân bay này thuộc thành phố nào? (Để search: Tìm vé đi Đà Nẵng -> Ra sân bay DAD)
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
}