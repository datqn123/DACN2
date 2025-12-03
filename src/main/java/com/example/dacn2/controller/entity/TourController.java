package com.example.dacn2.controller.entity;

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
public class TourController {

    @Autowired private TourService tourService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Tour> create(
            @RequestPart("tour") String tourRequestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {

        // Cấu hình ObjectMapper để đọc được LocalDate (yyyy-MM-dd)
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        TourRequest request = mapper.readValue(tourRequestJson, TourRequest.class);

        return ApiResponse.<Tour>builder()
                .result(tourService.create(request, images))
                .message("Tạo tour thành công")
                .build();
    }

    // Các hàm GET, PUT, DELETE bạn viết tương tự HotelController nhé
}