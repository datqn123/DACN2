package com.example.dacn2.controller.public_api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.BookingRequest;
import com.example.dacn2.dto.response.BookingResponse;
import com.example.dacn2.dto.response.VoucherResponse;
import com.example.dacn2.entity.booking.Booking;
import com.example.dacn2.service.entity.VoucherService;
import com.example.dacn2.service.page.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/public/booking")
@PreAuthorize("hasRole('USER')")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private VoucherService voucherService;

    @GetMapping("/vouchers/check-hotel")
    public ApiResponse<List<VoucherResponse>> getVouchersForHotel(
            @RequestParam Long hotelId,
            @RequestParam Double totalAmount) {
        return ApiResponse.<List<VoucherResponse>>builder()
                .result(bookingService.getVouchersForBooking(hotelId, totalAmount))
                .message("Lấy danh sách voucher thành công")
                .build();
    }

    @PostMapping()
    public ApiResponse<BookingResponse> createBooking(@RequestBody @Valid BookingRequest request) {
        Booking booking = bookingService.createBooking(request);
        return ApiResponse.<BookingResponse>builder()
                .result(BookingResponse.fromEntity(booking))
                .message("Đặt chỗ thành công! Vui lòng thanh toán.")
                .build();
    }

    // Lấy tất cả đơn đặt của user
    @GetMapping("/my-bookings")
    public ApiResponse<List<BookingResponse>> getMyBookings() {
        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookingService.getMyBookings())
                .message("Lấy danh sách đơn đặt thành công")
                .build();
    }

    // Lấy danh sách khách sạn đã đặt (chỉ booking HOTEL đã CONFIRMED)
    @GetMapping("/my-hotels")
    public ApiResponse<List<BookingResponse>> getMyHotelBookings() {
        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookingService.getMyHotelBookings())
                .message("Lấy danh sách khách sạn đã đặt thành công")
                .build();
    }

    // Tra cứu đơn hàng theo mã booking
    @GetMapping("/lookup/{bookingCode}")
    public ApiResponse<BookingResponse> lookupBooking(@PathVariable String bookingCode) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.lookupByCode(bookingCode))
                .message("Tra cứu đơn hàng thành công")
                .build();
    }

    // Lấy chi tiết đơn hàng theo ID
    @GetMapping("/{id}")
    public ApiResponse<BookingResponse> getBookingById(@PathVariable Long id) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getBookingById(id))
                .message("Lấy chi tiết đơn hàng thành công")
                .build();
    }
}
