package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.hotel.ReviewRequest;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.entity.hotel.HotelReview;
import com.example.dacn2.repository.BookingRepository;
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
    @Autowired
    private BookingRepository bookingRepository;

    // 1. Lấy review của khách sạn
    public Page<HotelReview> getReviewsByHotel(Long hotelId, Pageable pageable) {
        return reviewRepository.findByHotelId(hotelId, pageable);
    }

    // 2. Tạo Review Mới
    @Transactional
    public HotelReview createReview(ReviewRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new RuntimeException("Khách sạn không tồn tại"));

        if (!bookingRepository.hasUserBookedHotel(user.getId(), hotel.getId())) {
            throw new RuntimeException("Bạn cần đặt phòng và hoàn thành thanh toán trước khi đánh giá!");
        }

        if (reviewRepository.existsByUserIdAndHotelId(user.getId(), hotel.getId())) {
            throw new RuntimeException("Bạn đã đánh giá khách sạn này rồi! Vui lòng sửa đánh giá cũ.");
        }

        HotelReview review = new HotelReview();
        review.setUser(user);
        review.setHotel(hotel);
        mapReviewData(request, review);

        reviewRepository.save(review);

        updateHotelAggregateRating(hotel);

        return review;
    }

    // 3. Cập nhật Review (Chỉ owner mới được sửa)
    @Transactional
    public HotelReview updateReview(Long reviewId, ReviewRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        HotelReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền sửa đánh giá này!");
        }
        mapReviewData(request, review);
        reviewRepository.save(review);

        updateHotelAggregateRating(review.getHotel());

        return review;
    }

    // 4. Xóa Review (Chỉ owner mới được xóa)
    @Transactional
    public void deleteReview(Long reviewId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        HotelReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));

        // Kiểm tra quyền sở hữu
        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa đánh giá này!");
        }

        Hotel hotel = review.getHotel();
        reviewRepository.delete(review);

        // Cập nhật lại điểm hotel sau khi xóa
        updateHotelAggregateRating(hotel);
    }

    // 5. Lấy review của user cho hotel cụ thể
    public HotelReview getMyReviewForHotel(Long hotelId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        return reviewRepository.findByUserIdAndHotelId(user.getId(), hotelId)
                .orElse(null);
    }

    // Helper: Map dữ liệu từ request vào review
    private void mapReviewData(ReviewRequest request, HotelReview review) {
        review.setComment(request.getComment());
        review.setCleanlinessRating(request.getCleanliness());
        review.setComfortRating(request.getComfort());
        review.setLocationRating(request.getLocation());
        review.setStaffRating(request.getStaff());
        review.setFacilitiesRating(request.getFacilities());

        // Tính điểm trung bình cho review này
        double avg = (request.getCleanliness() + request.getComfort() +
                request.getLocation() + request.getStaff() + request.getFacilities()) / 5.0;
        review.setAverageRating(Math.round(avg * 10.0) / 10.0);
    }

    // Hàm tính lại điểm trung bình toàn khách sạn
    private void updateHotelAggregateRating(Hotel hotel) {
        List<HotelReview> allReviews = reviewRepository.findByHotelId(hotel.getId(), Pageable.unpaged()).getContent();

        if (allReviews.isEmpty()) {
            // Reset điểm về null nếu không còn review
            hotel.setTotalReviews(0);
            hotel.setAverageRating(null);
            hotel.setCleanlinessScore(null);
            hotel.setComfortScore(null);
            hotel.setLocationScore(null);
            hotel.setStaffScore(null);
            hotel.setFacilitiesScore(null);
            hotelRepository.save(hotel);
            return;
        }

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