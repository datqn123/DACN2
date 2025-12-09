package com.example.dacn2.controller.public_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.entity.flight.Flight;
import com.example.dacn2.service.entity.FlightService;

@RestController
@RequestMapping("/api/public/flights")
@PreAuthorize("hasRole('USER')")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping("/{id}")
    public ApiResponse<Flight> getDetail(@PathVariable Long id) {
        return ApiResponse.<Flight>builder()
                .result(flightService.getById(id))
                .build();
    }

}
