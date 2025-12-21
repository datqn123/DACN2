package com.example.dacn2.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.response.admin.TotalResponse;
import com.example.dacn2.dto.response.home.HotelSearchResponse;
import com.example.dacn2.service.Admin.AdminService;
import com.example.dacn2.service.entity.HotelService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private HotelService hotelService;

    @GetMapping("/total-info")
    public ApiResponse<TotalResponse> getTotal() {
        return ApiResponse.<TotalResponse>builder()
                .message("Get total successfully")
                .result(adminService.getTotal())
                .build();
    }

}
