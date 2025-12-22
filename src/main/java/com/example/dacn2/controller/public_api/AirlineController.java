package com.example.dacn2.controller.public_api;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.entity.flight.Airline;
import com.example.dacn2.service.entity.AirlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/airlines")
@RequiredArgsConstructor
public class AirlineController {

    private final AirlineService airlineService;

    @GetMapping
    public ApiResponse<List<Airline>> getAll() {
        return ApiResponse.<List<Airline>>builder()
                .result(airlineService.getAll())
                .message("Lấy danh sách hãng bay thành công")
                .build();
    }
}
