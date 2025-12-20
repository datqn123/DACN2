package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.SearchHistoryRequest;
import com.example.dacn2.entity.Location;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.User.SearchHistory;
import com.example.dacn2.entity.User.SearchType;
import com.example.dacn2.entity.hotel.HotelType;
import com.example.dacn2.repository.SearchHistoryRepository;
import com.example.dacn2.repository.auth.AccountRepositoryInterface;
import com.example.dacn2.repository.location.LocationInterfaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service lưu lịch sử tìm kiếm - CHỈ DÙNG NỘI BỘ cho Recommendation
 * Không expose API ra ngoài cho user xem
 */
@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final AccountRepositoryInterface accountRepository;
    private final LocationInterfaceRepository locationRepository;

    /**
     * Lưu lịch sử từ Request DTO
     * Dùng khi user tìm kiếm hotel/tour/flight
     * 
     * @return true nếu lưu thành công, false nếu skip (chưa login)
     */
    @Transactional
    public boolean saveFromRequest(Long accountId, SearchHistoryRequest request) {
        if (accountId == null || request == null || request.getKeyword() == null) {
            return false;
        }

        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            return false;
        }

        SearchType searchType = parseSearchType(request.getSearchType());
        HotelType hotelType = parseHotelType(request.getHotelType());

        saveOrUpdate(accountOpt.get(), request.getKeyword(), searchType,
                request.getResultCount(), request.getLocationId(),
                request.getMinPrice(), request.getMaxPrice(),
                request.getStarRating(), hotelType);

        return true;
    }

    private SearchType parseSearchType(String value) {
        if (value == null)
            return SearchType.HOTEL;
        try {
            return SearchType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SearchType.HOTEL;
        }
    }

    private HotelType parseHotelType(String value) {
        if (value == null)
            return null;
        try {
            return HotelType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void saveOrUpdate(Account account, String keyword, SearchType searchType,
            Integer resultCount, Long locationId,
            Double minPrice, Double maxPrice,
            Integer starRating, HotelType hotelType) {

        // Xóa nếu đã tồn tại (để cập nhật thời gian)
        Optional<SearchHistory> existing = searchHistoryRepository
                .findFirstByAccountIdAndKeywordAndSearchTypeOrderByCreatedAtDesc(account.getId(), keyword, searchType);
        existing.ifPresent(searchHistoryRepository::delete);

        Location location = locationId != null
                ? locationRepository.findById(locationId).orElse(null)
                : null;

        SearchHistory history = SearchHistory.builder()
                .account(account)
                .keyword(keyword)
                .searchType(searchType)
                .resultCount(resultCount)
                .location(location)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .starRating(starRating)
                .hotelType(hotelType)
                .build();

        searchHistoryRepository.save(history);
    }
}
