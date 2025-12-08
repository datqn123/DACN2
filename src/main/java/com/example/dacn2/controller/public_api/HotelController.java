package com.example.dacn2.controller.public_api;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.hotel.HotelFilterRequest;
import com.example.dacn2.dto.response.home.HotelSearchResponse;
import com.example.dacn2.dto.response.home.LocationSearchResult;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.HotelType;
import com.example.dacn2.service.entity.HotelService;
import com.example.dacn2.service.user_service.SearchHotelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/hotels")
public class HotelController {

        @Autowired
        private SearchHotelService searchHotelService;
        @Autowired
        private HotelService hotelService;

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

        @GetMapping("/{id}")
        public ApiResponse<Hotel> getDetail(@PathVariable Long id) {
                return ApiResponse.<Hotel>builder()
                                .result(hotelService.getById(id))
                                .build();
        }

        @GetMapping("/search")
        public ApiResponse<HotelSearchResponse> searchHotels(
                        @RequestParam(required = false) String slug,
                        @RequestParam(required = false) Double minPrice,
                        @RequestParam(required = false) Double maxPrice,
                        @RequestParam(required = false) Integer minStarRating,
                        @RequestParam(required = false) Integer maxStarRating,
                        @RequestParam(required = false) HotelType hotelType,
                        @RequestParam(required = false) String sortByPrice,
                        @RequestParam(required = false, defaultValue = "0") Integer page) {

                HotelFilterRequest filter = HotelFilterRequest.builder()
                                .locationSlug(slug)
                                .minPrice(minPrice)
                                .maxPrice(maxPrice)
                                .minStarRating(minStarRating)
                                .maxStarRating(maxStarRating)
                                .hotelType(hotelType)
                                .sortByPrice(sortByPrice)
                                .page(page)
                                .build();

                return ApiResponse.<HotelSearchResponse>builder()
                                .result(searchHotelService.searchHotelsWithFilter(filter))
                                .message("Tìm kiếm khách sạn thành công")
                                .build();
        }
}