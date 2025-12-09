package com.example.dacn2.controller.public_api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.response.home.VoucherCardResponse;
import com.example.dacn2.entity.voucher.Voucher;
import com.example.dacn2.service.entity.VoucherService;

@RestController
@RequestMapping("/api/public/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @GetMapping("/{id}")
    public ApiResponse<Voucher> getDetail(@PathVariable Long id) {
        return ApiResponse.<Voucher>builder()
                .result(voucherService.getById(id))
                .build();
    }

    @GetMapping("/hotel-page")
    public ApiResponse<List<VoucherCardResponse>> getVoucherToHotelPage() {
        return ApiResponse.<List<VoucherCardResponse>>builder()
                .result(voucherService.getVoucherToHotelPage())
                .build();
    }
}
