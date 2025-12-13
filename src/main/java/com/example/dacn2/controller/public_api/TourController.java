package com.example.dacn2.controller.public_api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.entity.tour.Tour;
import com.example.dacn2.service.entity.TourService;

@RestController
@RequestMapping("/api/public/tours")
public class TourController {

    @Autowired
    private TourService tourService;

    @GetMapping("/{id}")
    public ApiResponse<Tour> getDetail(@PathVariable Long id) {
        return ApiResponse.<Tour>builder()
                .result(tourService.getById(id))
                .message("Lấy chi tiết tour thành công")
                .build();
    }

    @GetMapping
    public ApiResponse<List<Tour>> getAll() {
        return ApiResponse.<List<Tour>>builder()
                .result(tourService.getAll())
                .message("Lấy danh sách tour thành công")
                .build();
    }
}
