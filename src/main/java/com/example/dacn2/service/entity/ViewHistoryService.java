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
        findLatestView(accountId, hotelId).ifPresent(vh -> {
            vh.setClickedBooking(true);
            viewHistoryRepository.save(vh);
        });
    }

    @Transactional
    public void markClickedFavorite(Long accountId, Long hotelId) {
        findLatestView(accountId, hotelId).ifPresent(vh -> {
            vh.setClickedFavorite(true);
            viewHistoryRepository.save(vh);
        });
    }

    // Lấy lịch sử xem của user
    public Page<ViewHistory> getRecentViews(Long accountId, int page, int size) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null)
            return Page.empty();
        return viewHistoryRepository.findByAccountOrderByViewedAtDesc(account, PageRequest.of(page, size));
    }

    // Helper methods
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
