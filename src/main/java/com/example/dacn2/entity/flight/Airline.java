package com.example.dacn2.entity.flight;

import com.example.dacn2.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "airlines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Airline extends BaseEntity {

    private String name; // VD: "Vietnam Airlines"

    private String code; // VD: "VN", "VJ"

    private String logoUrl; // Logo hiển thị trên UI
}