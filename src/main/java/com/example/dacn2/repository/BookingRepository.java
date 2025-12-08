package com.example.dacn2.repository;

import com.example.dacn2.entity.booking.Booking;
import com.example.dacn2.entity.User.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Lấy lịch sử đặt vé của 1 user
    List<Booking> findByUser(Account user);

    // Tìm theo mã đơn hàng
    Booking findByBookingCode(String bookingCode);

    @Query("SELECT COUNT(b) FROM Booking b " +
            "WHERE b.room.id = :roomId " +
            "AND b.status != 'CANCELLED' " +
            "AND b.checkInDate < :checkOutDate " +
            "AND b.checkOutDate > :checkInDate")
    Long countBookedRooms(Long roomId, LocalDateTime checkInDate, LocalDateTime checkOutDate);
}