package com.example.dacn2.controller.public_api;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.hotel.ReviewRequest;
import com.example.dacn2.entity.hotel.HotelReview;
import com.example.dacn2.service.entity.ReviewService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // 1. Viết Review (Cần đăng nhập)
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse<HotelReview> createReview(@RequestBody @Valid ReviewRequest request) {
        return ApiResponse.<HotelReview>builder()
                .result(reviewService.createReview(request))
                .message("Đánh giá thành công")
                .build();
    }

    // 2. Xem Review của khách sạn (Public)
    // GET /api/reviews/hotel/1?page=0&size=5
    @GetMapping("/hotel/{hotelId}")
    public ApiResponse<Page<HotelReview>> getHotelReviews(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        // Sắp xếp review mới nhất lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ApiResponse.<Page<HotelReview>>builder()
                .result(reviewService.getReviewsByHotel(hotelId, pageable))
                .build();
    }
}