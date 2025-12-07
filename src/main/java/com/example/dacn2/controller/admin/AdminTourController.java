package com.example.dacn2.controller.admin;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.tour.TourRequest;
import com.example.dacn2.entity.tour.Tour;
import com.example.dacn2.service.entity.TourService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Import cái này để parse ngày tháng
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/tours")
public class AdminTourController {

    @Autowired
    private TourService tourService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Tour> create(
            @RequestPart("tour") String tourRequestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        // Cấu hình ObjectMapper để đọc được LocalDate (yyyy-MM-dd)
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        TourRequest request = mapper.readValue(tourRequestJson, TourRequest.class);

        return ApiResponse.<Tour>builder()
                .result(tourService.create(request, images))
                .message("Tạo tour thành công")
                .build();
    }

    // GET ALL - Lấy danh sách tất cả tour
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Tour>> getAll() {
        return ApiResponse.<List<Tour>>builder()
                .result(tourService.getAll())
                .message("Lấy danh sách tour thành công")
                .build();
    }

    // GET DETAIL - Lấy chi tiết một tour theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Tour> getDetail(@PathVariable Long id) {
        return ApiResponse.<Tour>builder()
                .result(tourService.getById(id))
                .message("Lấy chi tiết tour thành công")
                .build();
    }

    // UPDATE - Cập nhật tour theo ID
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Tour> update(
            @PathVariable Long id,
            @RequestPart("tour") String tourRequestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        TourRequest request = mapper.readValue(tourRequestJson, TourRequest.class);

        return ApiResponse.<Tour>builder()
                .result(tourService.update(id, request, images))
                .message("Cập nhật tour thành công")
                .build();
    }

    // DELETE - Xóa tour theo ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        tourService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Xóa tour thành công")
                .build();
    }
}