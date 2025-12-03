package com.example.dacn2.controller;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.flight.FlightRequest;
import com.example.dacn2.entity.flight.Flight;
import com.example.dacn2.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/flights")
public class FlightController {

    @Autowired private FlightService flightService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Flight> create(@RequestBody FlightRequest request) {
        return ApiResponse.<Flight>builder()
                .result(flightService.create(request))
                .message("Tạo chuyến bay thành công")
                .build();
    }

    @GetMapping
    public ApiResponse<List<Flight>> getAll() {
        return ApiResponse.<List<Flight>>builder()
                .result(flightService.getAll())
                .build();
    }
}