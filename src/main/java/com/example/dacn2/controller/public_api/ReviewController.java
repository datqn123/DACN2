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

    // 1. Viết Review (Cần đăng nhập + đã từng đặt phòng)
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse<HotelReview> createReview(@RequestBody @Valid ReviewRequest request) {
        return ApiResponse.<HotelReview>builder()
                .result(reviewService.createReview(request))
                .message("Đánh giá thành công")
                .build();
    }

    // 2. Cập nhật Review (Chỉ owner mới được sửa)
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse<HotelReview> updateReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewRequest request) {
        return ApiResponse.<HotelReview>builder()
                .result(reviewService.updateReview(reviewId, request))
                .message("Cập nhật đánh giá thành công")
                .build();
    }

    // 3. Xóa Review (Chỉ owner mới được xóa)
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ApiResponse.<Void>builder()
                .message("Xóa đánh giá thành công")
                .build();
    }

    // 4. Xem Review của khách sạn (Public)
    @GetMapping("/hotel/{hotelId}")
    public ApiResponse<Page<HotelReview>> getHotelReviews(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ApiResponse.<Page<HotelReview>>builder()
                .result(reviewService.getReviewsByHotel(hotelId, pageable))
                .build();
    }

    // 5. Lấy review của mình cho hotel (để hiển thị form edit)
    @GetMapping("/my-review/{hotelId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse<HotelReview> getMyReviewForHotel(@PathVariable Long hotelId) {
        return ApiResponse.<HotelReview>builder()
                .result(reviewService.getMyReviewForHotel(hotelId))
                .message("Lấy đánh giá thành công")
                .build();
    }
}