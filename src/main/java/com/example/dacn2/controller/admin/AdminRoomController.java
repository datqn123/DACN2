package com.example.dacn2.controller.admin;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.hotel.RoomRequest;
import com.example.dacn2.entity.hotel.Room;
import com.example.dacn2.service.entity.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rooms")
public class AdminRoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Room>> getAll() {
        return ApiResponse.<List<Room>>builder()
                .result(roomService.getAll())
                .build();
    }

    // API tìm phòng theo ID Khách sạn (Quan trọng để hiển thị)
    // GET /api/admin/rooms/hotel/1
    @GetMapping("/hotel/{hotelId}")
    public ApiResponse<List<Room>> getByHotel(@PathVariable Long hotelId) {
        return ApiResponse.<List<Room>>builder()
                .result(roomService.getByHotelId(hotelId))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Room> create(@RequestBody @Valid RoomRequest request) {
        return ApiResponse.<Room>builder()
                .result(roomService.create(request))
                .message("Thêm phòng thành công")
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Room> update(@PathVariable Long id, @RequestBody @Valid RoomRequest request) {
        return ApiResponse.<Room>builder()
                .result(roomService.update(id, request))
                .message("Cập nhật phòng thành công")
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Xóa phòng thành công")
                .build();
    }
}