package com.example.dacn2.controller.public_api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.entity.hotel.Room;
import com.example.dacn2.service.entity.RoomService;

@RestController
@RequestMapping("/api/public/rooms")
@PreAuthorize("hasRole('USER')")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping("/{id}")
    public ApiResponse<Room> getDetail(@PathVariable Long id) {
        return ApiResponse.<Room>builder()
                .result(roomService.getById(id))
                .build();
    }

    @GetMapping("/{hotelId}")
    public ApiResponse<List<Room>> getDetailByHotelId(@PathVariable Long hotelId) {
        return ApiResponse.<List<Room>>builder()
                .result(roomService.getByHotelId(hotelId))
                .build();
    }
}
