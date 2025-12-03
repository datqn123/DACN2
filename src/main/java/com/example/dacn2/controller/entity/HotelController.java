package com.example.dacn2.controller.entity;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.hotel.HotelRequest;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.service.entity.HotelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/hotels")
public class HotelController {

    @Autowired private HotelService hotelService;

    // 1. Lấy danh sách (Giữ nguyên)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Hotel>> getAll() {
        return ApiResponse.<List<Hotel>>builder()
                .result(hotelService.getAll())
                .build();
    }

    // 2. Lấy chi tiết (Giữ nguyên)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Hotel> getDetail(@PathVariable Long id) {
        return ApiResponse.<Hotel>builder()
                .result(hotelService.getById(id))
                .build();
    }

    // 3. TẠO MỚI (Sửa để nhận File + JSON)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Hotel> create(
            @RequestPart("hotel") String hotelRequestJson, // Nhận JSON dạng String
            @RequestPart(value = "images", required = false) List<MultipartFile> images // Nhận File
    ) throws IOException {

        // Chuyển String JSON thành Object Java
        ObjectMapper mapper = new ObjectMapper();
        HotelRequest request = mapper.readValue(hotelRequestJson, HotelRequest.class);

        // Gọi Service
        Hotel result = hotelService.create(request, images);

        return ApiResponse.<Hotel>builder()
                .result(result)
                .message("Tạo khách sạn và upload ảnh thành công")
                .build();
    }

    // 4. CẬP NHẬT (Sửa để nhận File + JSON)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Hotel> update(
            @PathVariable Long id,
            @RequestPart("hotel") String hotelRequestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {

        // Chuyển String JSON thành Object Java
        ObjectMapper mapper = new ObjectMapper();
        HotelRequest request = mapper.readValue(hotelRequestJson, HotelRequest.class);

        // Gọi Service
        Hotel result = hotelService.update(id, request, images);

        return ApiResponse.<Hotel>builder()
                .result(result)
                .message("Cập nhật khách sạn thành công")
                .build();
    }

    // 5. Xóa (Giữ nguyên)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        hotelService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Xóa khách sạn thành công")
                .build();
    }
}