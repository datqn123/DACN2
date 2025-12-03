package com.example.dacn2.dto.request.flight;
import lombok.Data;

@Data
public class FlightSeatRequest {
    private String seatClass; // ECONOMY, BUSINESS...
    private Double price;
    private Integer quantity; // Số lượng ghế

    // Quyền lợi
    private String cabinBaggage; // 7kg
    private String checkedBaggage; // 20kg
    private Boolean isRefundable;
    private Boolean isChangeable;
    private Boolean hasMeal;
}