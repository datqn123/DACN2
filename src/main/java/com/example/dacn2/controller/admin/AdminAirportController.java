package com.example.dacn2.controller.admin;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.flight.AirportRequest;
import com.example.dacn2.entity.flight.Airport;
import com.example.dacn2.service.entity.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/airports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAirportController {

    private final AirportService airportService;

    @GetMapping
    public ApiResponse<List<Airport>> getAll() {
        return ApiResponse.<List<Airport>>builder()
                .result(airportService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<Airport> getById(@PathVariable Long id) {
        return ApiResponse.<Airport>builder()
                .result(airportService.getById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<Airport> create(@RequestBody AirportRequest request) {
        return ApiResponse.<Airport>builder()
                .result(airportService.create(request))
                .message("Tạo sân bay thành công")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<Airport> update(@PathVariable Long id, @RequestBody AirportRequest request) {
        return ApiResponse.<Airport>builder()
                .result(airportService.update(id, request))
                .message("Cập nhật sân bay thành công")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        airportService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Xóa sân bay thành công")
                .build();
    }
}
