package com.example.dacn2.service.entity;

import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.User.ViewHistory;
import com.example.dacn2.entity.User.ViewSource;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.repository.ViewHistoryRepository;
import com.example.dacn2.repository.auth.AccountRepositoryInterface;
import com.example.dacn2.repository.hotel.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewHistoryService {

    private final ViewHistoryRepository viewHistoryRepository;
    private final AccountRepositoryInterface accountRepository;
    private final HotelRepository hotelRepository;

    // Helper: Lấy accountId từ UserDetails
    public Long getAccountIdFromUserDetails(UserDetails userDetails) {
        if (userDetails == null)
            return null;
        return accountRepository.findByEmail(userDetails.getUsername())
                .map(Account::getId).orElse(null);
    }

    // Ghi nhận lượt xem hotel
    @Transactional
    public ViewHistory trackView(Long accountId, Long hotelId, String source, String searchQuery) {
        Account account = accountRepository.findById(accountId).orElse(null);
        Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
        if (account == null || hotel == null)
            return null;

        ViewHistory viewHistory = ViewHistory.builder()
                .account(account)
                .hotel(hotel)
                .viewedAt(LocalDateTime.now())
                .viewSource(parseViewSource(source))
                .searchQuery(searchQuery)
                // Snapshot hotel info
                .location(hotel.getLocation())
                .hotelStarRating(hotel.getStarRating())
                .hotelType(hotel.getType())
                .hotelPriceRange(hotel.getPriceRange())
                .hotelPricePerNight(hotel.getPricePerNightFrom())
                .hotelAverageRating(hotel.getAverageRating())
                .build();

        log.info("Tracked view: accountId={}, hotelId={}", accountId, hotelId);
        return viewHistoryRepository.save(viewHistory);
    }

    // Cập nhật thời gian xem
    @Transactional
    public void updateViewDuration(Long viewHistoryId, Integer seconds) {
        viewHistoryRepository.findById(viewHistoryId).ifPresent(vh -> {
            vh.setViewDurationSeconds(seconds);
            viewHistoryRepository.save(vh);
        });
    }

    // Đánh dấu actions
    @Transactional
    public void markClickedBooking(Long accountId, Long hotelId) {
        log.info("markClickedBooking called: accountId={}, hotelId={}", accountId, hotelId);
        ViewHistory vh = getOrCreateViewHistory(accountId, hotelId, ViewSource.DIRECT);
        if (vh != null) {
            vh.setClickedBooking(true);
            viewHistoryRepository.save(vh);
            log.info("Marked booking: viewHistoryId={}", vh.getId());
        }
    }

    @Transactional
    public void markClickedFavorite(Long accountId, Long hotelId) {
        log.info("markClickedFavorite called: accountId={}, hotelId={}", accountId, hotelId);
        ViewHistory vh = getOrCreateViewHistory(accountId, hotelId, ViewSource.FAVORITE);
        if (vh != null) {
            vh.setClickedFavorite(true);
            viewHistoryRepository.save(vh);
            log.info("Marked favorite: viewHistoryId={}", vh.getId());
        }
    }

    @Transactional
    public void markCompletedPayment(Long accountId, Long hotelId) {
        log.info("markCompletedPayment called: accountId={}, hotelId={}", accountId, hotelId);
        ViewHistory vh = getOrCreateViewHistory(accountId, hotelId, ViewSource.DIRECT);
        if (vh != null) {
            vh.setCompletedPayment(true);
            viewHistoryRepository.save(vh);
            log.info("Marked payment completed: viewHistoryId={}", vh.getId());
        }
    }

    @Transactional
    public void markSubmittedReview(Long accountId, Long hotelId) {
        log.info("markSubmittedReview called: accountId={}, hotelId={}", accountId, hotelId);
        ViewHistory vh = getOrCreateViewHistory(accountId, hotelId, ViewSource.DIRECT);
        if (vh != null) {
            vh.setSubmittedReview(true);
            viewHistoryRepository.save(vh);
            log.info("Marked review submitted: viewHistoryId={}", vh.getId());
        }
    }

    // Lấy lịch sử xem của user
    public Page<ViewHistory> getRecentViews(Long accountId, int page, int size) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null)
            return Page.empty();
        return viewHistoryRepository.findByAccountOrderByViewedAtDesc(account, PageRequest.of(page, size));
    }

    // ==================== HELPER METHODS ====================

    /**
     * Lấy ViewHistory mới nhất hoặc tạo mới nếu chưa có
     */
    private ViewHistory getOrCreateViewHistory(Long accountId, Long hotelId, ViewSource source) {
        var viewOpt = findLatestView(accountId, hotelId);
        if (viewOpt.isPresent()) {
            return viewOpt.get();
        }

        // Tạo ViewHistory mới
        Account account = accountRepository.findById(accountId).orElse(null);
        Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
        if (account == null || hotel == null) {
            log.warn("Cannot create ViewHistory - account or hotel not found: accountId={}, hotelId={}", accountId,
                    hotelId);
            return null;
        }

        ViewHistory newVh = ViewHistory.builder()
                .account(account)
                .hotel(hotel)
                .viewedAt(LocalDateTime.now())
                .viewSource(source)
                // Snapshot hotel info
                .location(hotel.getLocation())
                .hotelStarRating(hotel.getStarRating())
                .hotelType(hotel.getType())
                .hotelPriceRange(hotel.getPriceRange())
                .hotelPricePerNight(hotel.getPricePerNightFrom())
                .hotelAverageRating(hotel.getAverageRating())
                .build();
        ViewHistory saved = viewHistoryRepository.save(newVh);
        log.info("Created new ViewHistory: viewHistoryId={}, source={}", saved.getId(), source);
        return saved;
    }

    private java.util.Optional<ViewHistory> findLatestView(Long accountId, Long hotelId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
        if (account == null || hotel == null)
            return java.util.Optional.empty();
        return viewHistoryRepository.findFirstByAccountAndHotelOrderByViewedAtDesc(account, hotel);
    }

    private ViewSource parseViewSource(String source) {
        if (source == null)
            return ViewSource.DIRECT;
        try {
            return ViewSource.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ViewSource.DIRECT;
        }
    }
}
