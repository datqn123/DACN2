package com.example.dacn2.entity.flight;

import com.example.dacn2.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "flights", indexes = {
        @Index(name = "idx_flight_airline", columnList = "airline_id"),
        @Index(name = "idx_flight_departure", columnList = "departureTime")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flight extends BaseEntity {

    private String flightNumber; // VD: "VN123"

    // Hãng bay nào?
    @ManyToOne
    @JoinColumn(name = "airline_id")
    private Airline airline;

    // Bay từ đâu?
    @ManyToOne
    @JoinColumn(name = "departure_airport_id")
    private Airport departureAirport;

    // Bay đến đâu?
    @ManyToOne
    @JoinColumn(name = "arrival_airport_id")
    private Airport arrivalAirport;

    // Thời gian cất cánh/hạ cánh
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    // Thời gian bay (Tính sẵn để hiển thị cho nhanh: "1h 30m")
    private String duration;

    private String image;
    // Một chuyến bay có nhiều hạng vé (Economy, Business...)
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private List<FlightSeat> flightSeats;
}