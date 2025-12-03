package com.example.dacn2.service;

import com.example.dacn2.dto.request.hotel.RoomRequest;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.Room;
import com.example.dacn2.repository.hotel.HotelRepository;
import com.example.dacn2.repository.hotel.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    @Autowired private RoomRepository roomRepository;
    @Autowired private HotelRepository hotelRepository;

    // 1. Lấy tất cả phòng (Dành cho Admin)
    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    // 2. Lấy phòng theo Khách sạn (Dành cho trang chi tiết khách sạn)
    public List<Room> getByHotelId(Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    // 3. Lấy chi tiết 1 phòng
    public Room getById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ID: " + id));
    }

    // 4. Tạo mới
    @Transactional
    public Room create(RoomRequest request) {
        Room room = new Room();
        mapRequestToEntity(request, room);
        return roomRepository.save(room);
    }

    // 5. Cập nhật
    @Transactional
    public Room update(Long id, RoomRequest request) {
        Room room = getById(id);
        mapRequestToEntity(request, room);
        return roomRepository.save(room);
    }

    // 6. Xóa
    @Transactional
    public void delete(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Phòng không tồn tại");
        }
        roomRepository.deleteById(id);
    }

    // Hàm phụ trợ Map dữ liệu
    private void mapRequestToEntity(RoomRequest request, Room room) {
        room.setName(request.getName());
        room.setPrice(request.getPrice());
        room.setCapacity(request.getCapacity());
        room.setQuantity(request.getQuantity());
        room.setArea(request.getArea());

        if (request.getIsAvailable() != null) {
            room.setIsAvailable(request.getIsAvailable());
        }

        // Tìm và gán khách sạn (Chỉ gán nếu hotelId thay đổi hoặc tạo mới)
        if (room.getHotel() == null || !room.getHotel().getId().equals(request.getHotelId())) {
            Hotel hotel = hotelRepository.findById(request.getHotelId())
                    .orElseThrow(() -> new RuntimeException("Khách sạn ID " + request.getHotelId() + " không tồn tại"));
            room.setHotel(hotel);
        }
    }
}