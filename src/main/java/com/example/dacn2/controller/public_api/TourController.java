package com.example.dacn2.controller.public_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.tour.TourFilterRequest;
import com.example.dacn2.dto.response.home.TourCardResponse;
import com.example.dacn2.dto.response.home.TourSearchResponse;
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
        public ApiResponse<Page<TourCardResponse>> getAll(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDir) {
                Sort sort = sortDir.equalsIgnoreCase("asc")
                                ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();
                Pageable pageable = PageRequest.of(page, size, sort);

                return ApiResponse.<Page<TourCardResponse>>builder()
                                .result(tourService.getAllPaged(pageable))
                                .message("Lấy danh sách tour thành công")
                                .build();
        }

        @GetMapping("/search")
        public ApiResponse<TourSearchResponse> searchTours(
                        @RequestParam(required = false) Long destinationId,
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) Double minPrice,
                        @RequestParam(required = false) Double maxPrice,
                        @RequestParam(required = false) String durationCategory,
                        @RequestParam(required = false) String sortByPrice,
                        @RequestParam(required = false, defaultValue = "0") Integer page) {
                TourFilterRequest filter = TourFilterRequest.builder()
                                .destinationId(destinationId)
                                .name(name)
                                .minPrice(minPrice)
                                .maxPrice(maxPrice)
                                .durationCategory(durationCategory)
                                .sortByPrice(sortByPrice)
                                .page(page)
                                .build();

                return ApiResponse.<TourSearchResponse>builder()
                                .result(tourService.searchToursWithFilter(filter))
                                .message("Tìm kiếm tour thành công")
                                .build();
        }
}
