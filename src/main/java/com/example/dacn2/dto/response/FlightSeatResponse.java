package com.example.dacn2.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightSeatResponse {
    private String seatClass;
    private Double price;
    private Integer availableQuantity;

    // Quyền lợi
    private String cabinBaggage;
    private String checkedBaggage;
    private Boolean isRefundable;
    private Boolean isChangeable;
    private Boolean hasMeal;
}
