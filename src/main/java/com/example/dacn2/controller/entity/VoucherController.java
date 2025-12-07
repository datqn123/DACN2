package com.example.dacn2.controller.entity;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.voucher.VoucherRequest;
import com.example.dacn2.dto.response.VoucherResponse;
import com.example.dacn2.entity.voucher.Voucher;
import com.example.dacn2.service.vocher_service.VoucherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<VoucherResponse>> getAll() {
        return ApiResponse.<List<VoucherResponse>>builder()
                .result(voucherService.getAll())
                .build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Voucher> create(
            @RequestPart("voucher") String voucherJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Để đọc LocalDateTime
        VoucherRequest request = mapper.readValue(voucherJson, VoucherRequest.class);

        return ApiResponse.<Voucher>builder()
                .result(voucherService.create(request, image))
                .message("Tạo voucher thành công")
                .build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Voucher> update(
            @PathVariable Long id,
            @RequestPart("voucher") String voucherJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        VoucherRequest request = mapper.readValue(voucherJson, VoucherRequest.class);

        return ApiResponse.<Voucher>builder()
                .result(voucherService.update(id, request, image))
                .message("Cập nhật voucher thành công")
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        voucherService.delete(id);
        return ApiResponse.<Void>builder().message("Xóa voucher thành công").build();
    }
}