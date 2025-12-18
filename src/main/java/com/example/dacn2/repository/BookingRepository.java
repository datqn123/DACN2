package com.example.dacn2.repository;

import com.example.dacn2.entity.booking.Booking;
import com.example.dacn2.entity.User.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
        List<Booking> findByUser(Account user);

        List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

        Booking findByBookingCode(String bookingCode);

        @Query("SELECT COUNT(b) FROM Booking b " +
                        "WHERE b.room.id = :roomId " +
                        "AND b.status != 'CANCELLED' " +
                        "AND b.checkInDate < :checkOutDate " +
                        "AND b.checkOutDate > :checkInDate")
        Long countBookedRooms(Long roomId, LocalDateTime checkInDate, LocalDateTime checkOutDate);

        // đếm số phòng đang có sẵn real-time
        @Query("SELECT COALESCE(SUM(r.quantity), 0) - COALESCE(COUNT(b), 0) FROM Room r " +
                        "LEFT JOIN Booking b ON b.room.id = r.id " +
                        "AND b.status != 'CANCELLED' " +
                        "AND b.checkInDate < :checkOutDate " +
                        "AND b.checkOutDate > :checkInDate " +
                        "WHERE r.hotel.id = :hotelId AND r.isAvailable = true")
        Long countAvailableRoomsForHotel(Long hotelId, LocalDateTime checkInDate, LocalDateTime checkOutDate);

        /**
         * Lấy danh sách hotelId có phòng còn trống trong khoảng ngày
         */
        @Query("SELECT DISTINCT r.hotel.id FROM Room r " +
                        "WHERE r.isAvailable = true " +
                        "AND r.quantity > (SELECT COUNT(b) FROM Booking b " +
                        "   WHERE b.room.id = r.id " +
                        "   AND b.status != 'CANCELLED' " +
                        "   AND b.checkInDate < :checkOutDate " +
                        "   AND b.checkOutDate > :checkInDate)")
        List<Long> findHotelIdsWithAvailableRooms(LocalDateTime checkInDate, LocalDateTime checkOutDate);

        // Kiểm tra user đã từng đặt phòng ở hotel này và đã thanh toán chưa
        @Query("SELECT COUNT(b) > 0 FROM Booking b " +
                        "WHERE b.user.id = :userId " +
                        "AND b.room.hotel.id = :hotelId " +
                        "AND b.status = 'CONFIRMED' " +
                        "AND b.isPaid = true")
        boolean hasUserBookedHotel(Long userId, Long hotelId);

        // Fetch booking với tất cả relationships cần thiết cho email
        @Query("SELECT b FROM Booking b " +
                        "LEFT JOIN FETCH b.room r " +
                        "LEFT JOIN FETCH r.hotel h " +
                        "LEFT JOIN FETCH b.flight f " +
                        "LEFT JOIN FETCH b.flightSeat fs " +
                        "LEFT JOIN FETCH b.tour t " +
                        "LEFT JOIN FETCH b.tourSchedule ts " +
                        "LEFT JOIN FETCH b.voucher v " +
                        "WHERE b.id = :bookingId")
        Optional<Booking> findByIdWithDetails(Long bookingId);
}
