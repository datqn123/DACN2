package com.example.dacn2.controller.public_api;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.entity.Location;
import com.example.dacn2.service.entity.LocationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    // --- PUBLIC API (Ai cũng xem được) ---

    @GetMapping("/{id}")
    public ApiResponse<Location> getDetail(@PathVariable Long id) {
        return ApiResponse.<Location>builder()
                .result(locationService.getById(id))
                .build();
    }

    @GetMapping("/child/{parent_slug}")
    public ApiResponse<List<Location>> getChildLocationByParentSlug(@PathVariable String parent_slug) {
        return ApiResponse.<List<Location>>builder()
                .result(locationService.getChildLocationByParentSlug(parent_slug))
                .build();
    }

    @GetMapping("/country")
    public ApiResponse<List<Location>> getCountryToHotelPage() {
        return ApiResponse.<List<Location>>builder()
                .result(locationService.getCountryToHotelPage())
                .build();
    }

    @GetMapping("/featured-to-hotel-page")
    public ApiResponse<List<Location>> getFeaturedLocationsToHotelPage() {
        return ApiResponse.<List<Location>>builder()
                .result(locationService.getFeaturedLocationsToHotelPage())
                .build();
    }
}