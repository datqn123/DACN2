package com.example.dacn2.controller.public_api;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.response.ViewHistoryResponse;
import com.example.dacn2.entity.User.ViewHistory;
import com.example.dacn2.service.entity.ViewHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/view-history")
@RequiredArgsConstructor
public class ViewHistoryController {

    private final ViewHistoryService viewHistoryService;

    // Ghi nhận lượt xem hotel
    @PostMapping("/track")
    public ApiResponse<Long> trackView(
            @RequestParam Long hotelId,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String searchQuery,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long accountId = viewHistoryService.getAccountIdFromUserDetails(userDetails);
        if (accountId == null) {
            return ApiResponse.<Long>builder().code(401).message("Chưa đăng nhập").build();
        }

        ViewHistory vh = viewHistoryService.trackView(accountId, hotelId, source, searchQuery);
        return ApiResponse.<Long>builder()
                .result(vh != null ? vh.getId() : null)
                .message("Đã ghi nhận")
                .build();
    }

    // Cập nhật thời gian xem
    @PutMapping("/{id}/duration")
    public ApiResponse<Void> updateDuration(@PathVariable Long id, @RequestParam Integer seconds) {
        viewHistoryService.updateViewDuration(id, seconds);
        return ApiResponse.<Void>builder().message("OK").build();
    }

    // Đánh dấu actions
    @PostMapping("/action/booking")
    public ApiResponse<Void> markBooking(@RequestParam Long hotelId, @AuthenticationPrincipal UserDetails userDetails) {
        Long accountId = viewHistoryService.getAccountIdFromUserDetails(userDetails);
        if (accountId != null)
            viewHistoryService.markClickedBooking(accountId, hotelId);
        return ApiResponse.<Void>builder().message("OK").build();
    }

    @PostMapping("/action/favorite")
    public ApiResponse<Void> markFavorite(@RequestParam Long hotelId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long accountId = viewHistoryService.getAccountIdFromUserDetails(userDetails);
        if (accountId != null)
            viewHistoryService.markClickedFavorite(accountId, hotelId);
        return ApiResponse.<Void>builder().message("OK").build();
    }

    // Lấy lịch sử xem
    @GetMapping("/recent")
    public ApiResponse<Page<ViewHistoryResponse>> getRecentViews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long accountId = viewHistoryService.getAccountIdFromUserDetails(userDetails);
        if (accountId == null) {
            return ApiResponse.<Page<ViewHistoryResponse>>builder().code(401).message("Chưa đăng nhập").build();
        }
        return ApiResponse.<Page<ViewHistoryResponse>>builder()
                .result(viewHistoryService.getRecentViews(accountId, page, size).map(ViewHistoryResponse::fromEntity))
                .build();
    }
}
