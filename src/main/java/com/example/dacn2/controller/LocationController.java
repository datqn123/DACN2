package com.example.dacn2.controller;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.location.LocationRequest;
import com.example.dacn2.entity.Location;
import com.example.dacn2.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    // --- PUBLIC API (Ai cũng xem được) ---

    @GetMapping
    public ApiResponse<List<Location>> getAll() {
        return ApiResponse.<List<Location>>builder()
                .result(locationService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<Location> getDetail(@PathVariable Long id) {
        return ApiResponse.<Location>builder()
                .result(locationService.getById(id))
                .build();
    }

    // --- ADMIN API (Cần quyền quản trị) ---

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Location> create(@RequestBody @Valid LocationRequest request) {
        return ApiResponse.<Location>builder()
                .result(locationService.create(request))
                .message("Tạo địa điểm thành công")
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Location> update(@PathVariable Long id, @RequestBody @Valid LocationRequest request) {
        return ApiResponse.<Location>builder()
                .result(locationService.update(id, request))
                .message("Cập nhật địa điểm thành công")
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        locationService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Xóa địa điểm thành công")
                .build();
    }
}