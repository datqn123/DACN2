package com.example.dacn2.dto.request.flight;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FlightSearchRequest {
    private Long departureLocationId; // ID địa điểm khởi hành (location)
    private Long arrivalLocationId; // ID địa điểm đến (location)
    private LocalDate departureDate; // Ngày đi (dd/MM/yyyy)
}
