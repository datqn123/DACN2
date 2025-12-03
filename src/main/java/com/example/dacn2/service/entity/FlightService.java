package com.example.dacn2.service;

import com.example.dacn2.dto.request.flight.FlightRequest;
import com.example.dacn2.dto.request.flight.FlightSeatRequest;
import com.example.dacn2.entity.flight.*;
import com.example.dacn2.repository.flight.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightService {

    @Autowired private FlightRepository flightRepository;
    @Autowired private AirlineRepository airlineRepository;
    @Autowired private AirportRepository airportRepository;

    @Transactional
    public Flight create(FlightRequest request) {
        Flight flight = new Flight();

        // 1. Gán thông tin cơ bản
        flight.setFlightNumber(request.getFlightNumber());
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());

        // Tự động tính thời gian bay (VD: "2h 15m")
        long minutes = Duration.between(request.getDepartureTime(), request.getArrivalTime()).toMinutes();
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        flight.setDuration(hours + "h " + remainingMinutes + "m");

        // 2. Tìm và gán Hãng bay
        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() -> new RuntimeException("Hãng bay không tồn tại"));
        flight.setAirline(airline);

        // 3. Tìm và gán Sân bay
        Airport depAirport = airportRepository.findById(request.getDepartureAirportId())
                .orElseThrow(() -> new RuntimeException("Sân bay đi không tồn tại"));
        Airport arrAirport = airportRepository.findById(request.getArrivalAirportId())
                .orElseThrow(() -> new RuntimeException("Sân bay đến không tồn tại"));

        flight.setDepartureAirport(depAirport);
        flight.setArrivalAirport(arrAirport);

        // 4. Xử lý Hạng ghế (Flight Seats)
        if (request.getSeats() != null) {
            List<FlightSeat> flightSeats = new ArrayList<>();
            for (FlightSeatRequest seatReq : request.getSeats()) {
                FlightSeat seat = new FlightSeat();
                seat.setSeatClass(seatReq.getSeatClass());
                seat.setPrice(seatReq.getPrice());
                seat.setAvailableQuantity(seatReq.getQuantity());

                // Map quyền lợi
                seat.setCabinBaggage(seatReq.getCabinBaggage());
                seat.setCheckedBaggage(seatReq.getCheckedBaggage());
                seat.setIsRefundable(seatReq.getIsRefundable());
                seat.setIsChangeable(seatReq.getIsChangeable());
                seat.setHasMeal(seatReq.getHasMeal());

                seat.setFlight(flight); // Gán quan hệ
                flightSeats.add(seat);
            }
            flight.setFlightSeats(flightSeats);
        }

        return flightRepository.save(flight);
    }

    public List<Flight> getAll() {
        return flightRepository.findAll();
    }
}