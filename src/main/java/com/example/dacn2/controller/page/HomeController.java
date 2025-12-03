package com.example.dacn2.controller;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.response.home.LocationCardResponse;
import com.example.dacn2.service.page.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/home") // Prefix chung cho trang chủ
public class HomeController {

    @Autowired
    private HomeService homeService;

    // API: Lấy địa điểm nổi bật
    // GET: http://localhost:8080/api/public/home/locations
    @GetMapping("/locations")
    public ApiResponse<List<LocationCardResponse>> getFeaturedLocations() {
        return ApiResponse.<List<LocationCardResponse>>builder()
                .result(homeService.getFeaturedLocations())
                .message("Lấy danh sách địa điểm nổi bật thành công")
                .build();
    }
}