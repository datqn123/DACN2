package com.example.dacn2.repository.hotel;

import com.example.dacn2.entity.hotel.HotelReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelReviewRepository extends JpaRepository<HotelReview, Long> {
    // Lấy danh sách review của 1 khách sạn (có phân trang)
    Page<HotelReview> findByHotelId(Long hotelId, Pageable pageable);
}