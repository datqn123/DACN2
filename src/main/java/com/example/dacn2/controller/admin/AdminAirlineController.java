package com.example.dacn2.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.flight.AirlineRequest;
import com.example.dacn2.entity.flight.Airline;
import com.example.dacn2.service.entity.AirlineService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/airlines")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAirlineController {

    @Autowired
    private AirlineService airlineService;

    @GetMapping
    public ApiResponse<List<Airline>> getAll() {
        return ApiResponse.<List<Airline>>builder()
                .result(airlineService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<Airline> getById(@PathVariable Long id) {
        return ApiResponse.<Airline>builder()
                .result(airlineService.getById(id))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Airline> create(@RequestBody AirlineRequest request) {
        return ApiResponse.<Airline>builder()
                .result(airlineService.create(request))
                .message("Tạo hãng bay thành công")
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Airline> update(@PathVariable Long id, @RequestBody AirlineRequest request) {
        return ApiResponse.<Airline>builder()
                .result(airlineService.update(id, request))
                .message("Cập nhật hãng bay thành công")
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        airlineService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Xóa hãng bay thành công")
                .build();
    }
}
