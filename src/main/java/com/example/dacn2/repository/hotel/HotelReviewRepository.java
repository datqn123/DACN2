package com.example.dacn2.repository.hotel;

import com.example.dacn2.entity.hotel.HotelReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelReviewRepository extends JpaRepository<HotelReview, Long> {
    // Lấy danh sách review của 1 khách sạn (có phân trang)
    Page<HotelReview> findByHotelId(Long hotelId, Pageable pageable);

    // Kiểm tra user đã review hotel này chưa
    boolean existsByUserIdAndHotelId(Long userId, Long hotelId);

    // Tìm review của user cho hotel
    Optional<HotelReview> findByUserIdAndHotelId(Long userId, Long hotelId);
}