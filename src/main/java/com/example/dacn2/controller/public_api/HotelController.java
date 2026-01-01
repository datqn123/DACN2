package com.example.dacn2.controller.public_api;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.hotel.HotelFilterRequest;
import com.example.dacn2.dto.response.home.HotelCardResponse;
import com.example.dacn2.dto.response.home.HotelSearchResponse;
import com.example.dacn2.dto.response.home.LocationSearchResult;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.HotelDocument;
import com.example.dacn2.entity.hotel.HotelType;
import com.example.dacn2.service.entity.HotelService;
import com.example.dacn2.service.user_service.SearchHotelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/public/hotels")
public class HotelController {

        @Autowired
        private SearchHotelService searchHotelService;
        @Autowired
        private HotelService hotelService;

        @GetMapping("/{id}")
        public ApiResponse<Hotel> getDetail(@PathVariable Long id) {
                return ApiResponse.<Hotel>builder()
                                .result(hotelService.getById(id))
                                .build();
        }

        // phân trang
        @GetMapping
        public ApiResponse<HotelSearchResponse> getAllNavigate(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "12") int size) {
                return ApiResponse.<HotelSearchResponse>builder()
                                .result(hotelService.getAllNavigate(page, size))
                                .message("Lấy danh sách khách sạn thành công")
                                .build();
        }

        @GetMapping("/search-hotel")
        public ApiResponse<List<HotelDocument>> searchHotelWithElasticsearch(
                        @RequestParam(required = false) String search) {
                return ApiResponse.<List<HotelDocument>>builder()
                                .result(hotelService.searchHotelWithEs(search))
                                .message("Tìm kiếm khách sạn thành công")
                                .build();
        }

        @GetMapping("/search")
        public ApiResponse<HotelSearchResponse> searchHotels(
                        @RequestParam(required = false) Long id,
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) Double minPrice,
                        @RequestParam(required = false) Double maxPrice,
                        @RequestParam(required = false) Integer minStarRating,
                        @RequestParam(required = false) Integer maxStarRating,
                        @RequestParam(required = false) HotelType hotelType,
                        @RequestParam(required = false) String sortByPrice,
                        @RequestParam(required = false, defaultValue = "0") Integer page,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                        @AuthenticationPrincipal UserDetails userDetails) {

                HotelFilterRequest filter = HotelFilterRequest.builder()
                                .id(id)
                                .name(name)
                                .minPrice(minPrice)
                                .maxPrice(maxPrice)
                                .minStarRating(minStarRating)
                                .maxStarRating(maxStarRating)
                                .hotelType(hotelType)
                                .sortByPrice(sortByPrice)
                                .page(page)
                                .checkInDate(checkInDate)
                                .checkOutDate(checkOutDate)
                                .build();

                Long accountId = searchHotelService.getAccountIdFromUserDetails(userDetails);
                HotelSearchResponse result = searchHotelService.searchHotelsAndSaveHistory(filter, accountId);

                return ApiResponse.<HotelSearchResponse>builder()
                                .result(result)
                                .message("Tìm kiếm khách sạn thành công")
                                .build();
        }

        @GetMapping("/by-location/{locationId}")
        public ApiResponse<List<HotelCardResponse>> getHotelByLocation(@PathVariable Long locationId) {
                return ApiResponse.<List<HotelCardResponse>>builder()
                                .result(hotelService.getHotelByLocation(locationId))
                                .build();
        }

}