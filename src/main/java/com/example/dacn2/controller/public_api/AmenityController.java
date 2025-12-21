package com.example.dacn2.controller.public_api;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.entity.hotel.Amenity;
import com.example.dacn2.repository.hotel.AmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/amenities")
public class AmenityController {

    @Autowired
    private AmenityRepository amenityRepository;

    @GetMapping
    public ApiResponse<List<Amenity>> getAllAmenities() {
        return ApiResponse.<List<Amenity>>builder()
                .result(amenityRepository.findAll())
                .message("Lấy danh sách tiện nghi thành công")
                .build();
    }
}
