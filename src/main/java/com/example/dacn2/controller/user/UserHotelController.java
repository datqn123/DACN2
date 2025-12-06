package com.example.dacn2.controller.user;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.hotel.HotelRequest;
import com.example.dacn2.dto.response.home.LocationSearchResult;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.service.hotel_service.HotelService;
import com.example.dacn2.service.hotel_service.SearchHotelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/public/hotels")
public class UserHotelController {

        @Autowired
        private HotelService hotelService;
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