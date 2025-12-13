package com.example.dacn2.controller.public_api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.response.FlightSeatResponse;
import com.example.dacn2.dto.response.FligthCardResponse;
import com.example.dacn2.entity.flight.Flight;
import com.example.dacn2.service.entity.FlightService;

@RestController
@RequestMapping("/api/public/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    // Lấy chi tiết chuyến bay
    @GetMapping("/{id}")
    public ApiResponse<Flight> getDetail(@PathVariable Long id) {
        return ApiResponse.<Flight>builder()
                .result(flightService.getById(id))
                .build();
    }

    // Lấy danh sách chuyến bay sắp khởi hành (cho trang chủ)
    @GetMapping("/cards")
    public ApiResponse<List<FligthCardResponse>> getFlightCards(
            @RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.<List<FligthCardResponse>>builder()
                .result(flightService.getFlightCardsForDisplay(limit))
                .message("Lấy danh sách chuyến bay thành công")
                .build();
    }

    // Tìm kiếm chuyến bay theo bộ lọc (tất cả params đều optional)
    @GetMapping("/search")
    public ApiResponse<List<FligthCardResponse>> searchFlights(
            @RequestParam(required = false) Long departureLocationId,
            @RequestParam(required = false) Long arrivalLocationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<Long> airlineIds) {

        return ApiResponse.<List<FligthCardResponse>>builder()
                .result(flightService.searchFlightsForDisplay(
                        departureLocationId,
                        arrivalLocationId,
                        departureDate,
                        minPrice,
                        maxPrice,
                        airlineIds))
                .message("Tìm kiếm chuyến bay thành công")
                .build();
    }

    // Lấy danh sách hạng ghế cho chuyến bay
    @GetMapping("/{id}/seat-classes")
    public ApiResponse<List<FlightSeatResponse>> getSeatClasses(@PathVariable Long id) {
        return ApiResponse.<List<FlightSeatResponse>>builder()
                .result(flightService.getSeatClasses(id))
                .message("Lấy danh sách hạng ghế thành công")
                .build();
    }
}
