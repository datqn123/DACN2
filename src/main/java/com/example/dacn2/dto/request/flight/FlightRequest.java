package com.example.dacn2.dto.request.flight;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FlightRequest {
    private String flightNumber; // VN123
    private Long airlineId;

    private Long departureAirportId;
    private Long arrivalAirportId;

    private LocalDateTime departureTime; // Định dạng JSON: "2025-12-20T08:00:00"
    private LocalDateTime arrivalTime;

    // Danh sách các hạng vé của chuyến bay này
    private List<FlightSeatRequest> seats;
}