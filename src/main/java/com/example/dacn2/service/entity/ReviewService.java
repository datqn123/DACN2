package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.hotel.ReviewRequest;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.HotelReview;
import com.example.dacn2.repository.auth.AccountRepositoryInterface;
import com.example.dacn2.repository.hotel.HotelRepository;
import com.example.dacn2.repository.hotel.HotelReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private HotelReviewRepository reviewRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private AccountRepositoryInterface accountRepository;

    // 1. Lấy review của khách sạn
    public Page<HotelReview> getReviewsByHotel(Long hotelId, Pageable pageable) {
        return reviewRepository.findByHotelId(hotelId, pageable);
    }

    // 2. Tạo Review Mới
    @Transactional
    public HotelReview createReview(ReviewRequest request) {
        // A. Lấy User đang đăng nhập (Từ Token)
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // B. Lấy Khách sạn
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new RuntimeException("Khách sạn không tồn tại"));

        // TODO: Kiểm tra xem User này đã từng đặt phòng ở đây chưa? (Booking)
        // Nếu chưa đặt thì không cho review (Tránh spam). Ta sẽ làm sau khi có module
        // Booking.

        // C. Tạo Review
        HotelReview review = new HotelReview();
        review.setUser(user);
        review.setHotel(hotel);
        review.setComment(request.getComment());

        // Gán điểm
        review.setCleanlinessRating(request.getCleanliness());
        review.setComfortRating(request.getComfort());
        review.setLocationRating(request.getLocation());
        review.setStaffRating(request.getStaff());
        review.setFacilitiesRating(request.getFacilities());

        // Tính điểm trung bình cho review này
        double avg = (request.getCleanliness() + request.getComfort() +
                request.getLocation() + request.getStaff() + request.getFacilities()) / 5.0;
        review.setAverageRating(Math.round(avg * 10.0) / 10.0); // Làm tròn 1 số thập phân

        // D. Lưu Review
        reviewRepository.save(review);

        // E. CẬP NHẬT LẠI ĐIỂM SỐ CHO KHÁCH SẠN (Aggregation)
        updateHotelAggregateRating(hotel);

        return review;
    }

    // Hàm tính lại điểm trung bình toàn khách sạn
    private void updateHotelAggregateRating(Hotel hotel) {
        List<HotelReview> allReviews = reviewRepository.findByHotelId(hotel.getId(), Pageable.unpaged()).getContent();

        if (allReviews.isEmpty())
            return;

        double totalClean = 0, totalComfort = 0, totalLoc = 0, totalStaff = 0, totalFac = 0, totalAvg = 0;
        int count = allReviews.size();

        for (HotelReview r : allReviews) {
            totalClean += r.getCleanlinessRating();
            totalComfort += r.getComfortRating();
            totalLoc += r.getLocationRating();
            totalStaff += r.getStaffRating();
            totalFac += r.getFacilitiesRating();
            totalAvg += r.getAverageRating();
        }

        // Cập nhật vào Entity Hotel
        hotel.setTotalReviews(count);
        hotel.setCleanlinessScore(Math.round((totalClean / count) * 10.0) / 10.0);
        hotel.setComfortScore(Math.round((totalComfort / count) * 10.0) / 10.0);
        hotel.setLocationScore(Math.round((totalLoc / count) * 10.0) / 10.0);
        hotel.setStaffScore(Math.round((totalStaff / count) * 10.0) / 10.0);
        hotel.setFacilitiesScore(Math.round((totalFac / count) * 10.0) / 10.0);

        // Điểm hiển thị chính (Average Rating)
        hotel.setAverageRating(Math.round((totalAvg / count) * 10.0) / 10.0);

        hotelRepository.save(hotel);
    }
}