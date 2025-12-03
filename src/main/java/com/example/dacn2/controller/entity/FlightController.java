package com.example.dacn2.controller.entity;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.flight.FlightRequest;
import com.example.dacn2.entity.flight.Flight;
import com.example.dacn2.service.entity.FlightService;
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
@RequestMapping("/api/admin/flights")
public class FlightController {

    @Autowired private FlightService flightService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Flight> create(
            @RequestPart("flight") String flightJson, // Nhận JSON string
            @RequestPart(value = "image", required = false) MultipartFile image // Nhận file ảnh
    ) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Để parse ngày tháng
        FlightRequest request = mapper.readValue(flightJson, FlightRequest.class);

        return ApiResponse.<Flight>builder()
                .result(flightService.create(request, image))
                .message("Tạo chuyến bay thành công")
                .build();
    }

    @GetMapping
    public ApiResponse<List<Flight>> getAll() {
        return ApiResponse.<List<Flight>>builder()
                .result(flightService.getAll())
                .build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Flight> update(
            @PathVariable Long id,
            @RequestPart("flight") String flightJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        FlightRequest request = mapper.readValue(flightJson, FlightRequest.class);

        return ApiResponse.<Flight>builder()
                .result(flightService.update(id, request, image))
                .message("Cập nhật chuyến bay thành công")
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ApiResponse<Flight> getDetail(@PathVariable Long id) {
        Flight flight = flightService.getById(id);
        return ApiResponse.<Flight>builder()
                .result(flight)
                .build();
    }

}