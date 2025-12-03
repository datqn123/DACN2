package com.example.dacn2.repository.hotel;

import com.example.dacn2.entity.hotel.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // Tìm danh sách phòng của một khách sạn cụ thể
    List<Room> findByHotelId(Long hotelId);
}