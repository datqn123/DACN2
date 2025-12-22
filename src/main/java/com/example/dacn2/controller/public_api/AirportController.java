package com.example.dacn2.controller.public_api;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.entity.flight.Airport;
import com.example.dacn2.service.entity.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/airports")
@RequiredArgsConstructor
public class AirportController {

    private final AirportService airportService;

    @GetMapping
    public ApiResponse<List<Airport>> getAll() {
        return ApiResponse.<List<Airport>>builder()
                .result(airportService.getAll())
                .message("Lấy danh sách sân bay thành công")
                .build();
    }
}
