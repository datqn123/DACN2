package com.example.dacn2.controller.public_api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.response.VoucherResponse;
import com.example.dacn2.service.page.BookingService;

@RestController
@RequestMapping("/api/public/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/vouchers/check-hotel")
    public ApiResponse<List<VoucherResponse>> getVouchersForHotel(
            @RequestParam Long hotelId,
            @RequestParam Double totalAmount) {
        return ApiResponse.<List<VoucherResponse>>builder()
                .result(bookingService.getVouchersForBooking(hotelId, totalAmount))
                .message("Lấy danh sách voucher thành công")
                .build();
    }
}
