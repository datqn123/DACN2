package com.example.dacn2.controller.public_api;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.response.home.LocationSearchResult;
import com.example.dacn2.service.user_service.SearchHotelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/hotels")
public class HotelController {

        @Autowired
        private SearchHotelService searchHotelService;

        // get top 10 location common
        @GetMapping("/top-10-locations")
        public ApiResponse<List<LocationSearchResult>> findTopDestinations() {
                return ApiResponse.<List<LocationSearchResult>>builder()
                                .result(searchHotelService.findTopDestinations())
                                .message("Get 10 location")
                                .build();
        }

        // API cho thanh tìm kiếm (Dropdown)
        // GET: /api/public/search/locations?keyword=Da
        @GetMapping("/search/locations")
        public ApiResponse<List<LocationSearchResult>> searchLocations(@RequestParam(required = false) String keyword) {
                return ApiResponse.<List<LocationSearchResult>>builder()
                                .result(searchHotelService.searchLocationDropdown(keyword))
                                .message("Lấy kết quả tìm kiếm thành công")
                                .build();
        }
}