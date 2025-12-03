package com.example.dacn2.entity.flight;

import com.example.dacn2.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flight_seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightSeat extends BaseEntity {

    private String seatClass; // VD: "Economy", "Business", "Economy Flex"

    private Double price; // Giá vé

    private Integer availableQuantity; // Số ghế còn trống

    // --- Các quyền lợi (Hiển thị tích xanh/đỏ trên UI) ---
    private String cabinBaggage; // VD: "7kg"
    private String checkedBaggage; // VD: "20kg" (hoặc null nếu không có)
    private Boolean isRefundable; // Hoàn vé?
    private Boolean isChangeable; // Đổi vé?
    private Boolean hasMeal;      // Có ăn nhẹ?

    @ManyToOne
    @JoinColumn(name = "flight_id")
    @JsonIgnore
    private Flight flight;
}