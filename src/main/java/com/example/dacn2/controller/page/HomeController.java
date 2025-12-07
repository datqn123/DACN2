package com.example.dacn2.controller.page;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.response.home.FlightCardResponse;
import com.example.dacn2.dto.response.home.HotelCardResponse;
import com.example.dacn2.dto.response.home.LocationCardResponse;
import com.example.dacn2.dto.response.home.LocationSearchResult;
import com.example.dacn2.dto.response.home.TourCardResponse;
import com.example.dacn2.service.hotel_service.HotelService;
import com.example.dacn2.service.hotel_service.SearchHotelService;
import com.example.dacn2.service.page.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/home") // Prefix chung cho trang chủ
public class HomeController {

    @Autowired
    private HomeService homeService;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private SearchHotelService searchHotelService;

    // API: Lấy địa điểm nổi bật
    // GET: http://localhost:8080/api/public/home/locations
    @GetMapping("/locations")
    public ApiResponse<List<LocationCardResponse>> getFeaturedLocations() {
        return ApiResponse.<List<LocationCardResponse>>builder()
                .result(homeService.getFeaturedLocations())
                .message("Lấy danh sách địa điểm nổi bật thành công")
                .build();
    }

    // Lấy flight
    @GetMapping("/flights")
    public ApiResponse<List<FlightCardResponse>> getFeaturedFlights() {
        return ApiResponse.<List<FlightCardResponse>>builder()
                .result(homeService.getFeaturedFlights())
                .message("Lấy danh sách vé máy bay giá tốt thành công")
                .build();
    }

    // lấy hotel
    @GetMapping("/hotels")
    public ApiResponse<List<HotelCardResponse>> getFeaturedHotels() {
        return ApiResponse.<List<HotelCardResponse>>builder()
                .result(homeService.getFeaturedHotels())
                .message("Lấy danh sách khách sạn giá tốt thành công")
                .build();
    }

    // lấy tour
    @GetMapping("/tours")
    public ApiResponse<List<TourCardResponse>> getFeaturedTours() {
        return ApiResponse.<List<TourCardResponse>>builder()
                .result(homeService.getFeaturedTours())
                .message("Lấy danh sách tour thành công")
                .build();
    }

    @GetMapping("/top-10-locations")
    public ApiResponse<List<LocationSearchResult>> findTopDestinations() {
        return ApiResponse.<List<LocationSearchResult>>builder()
                .result(searchHotelService.findTopDestinations())
                .message("Get 10 location")
                .build();
    }

    @GetMapping("/search/location/hotel")
    public ApiResponse<List<LocationSearchResult>> searchLocations(@RequestParam(required = false) String keyword) {
        return ApiResponse.<List<LocationSearchResult>>builder()
                .result(searchHotelService.searchLocationDropdown(keyword))
                .message("Lấy kết quả tìm kiếm thành công")
                .build();
    }
}
