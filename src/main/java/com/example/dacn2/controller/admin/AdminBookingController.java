package com.example.dacn2.controller.admin;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.response.BookingResponse;
import com.example.dacn2.entity.booking.BookingStatus;
import com.example.dacn2.service.page.BookingService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ApiResponse<List<BookingResponse>> getAll() {
        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookingService.getAllBookings())
                .message("Lấy danh sách đơn hàng thành công")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BookingResponse> getDetail(@PathVariable Long id) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getBookingById(id))
                .message("Lấy chi tiết đơn hàng thành công")
                .build();
    }

    @PutMapping("/{id}/status")
    public ApiResponse<BookingResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.updateBookingStatus(id, status))
                .message("Cập nhật trạng thái đơn hàng thành công")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<BookingResponse> updateBooking(
            @PathVariable Long id,
            @RequestBody com.example.dacn2.dto.request.BookingRequest request) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.updateBooking(id, request))
                .message("Cập nhật đơn hàng thành công")
                .build();
    }
}
