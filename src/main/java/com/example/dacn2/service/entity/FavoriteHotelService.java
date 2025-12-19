package com.example.dacn2.service.entity;

import com.example.dacn2.dto.response.home.HotelCardResponse;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.hotel.FavoriteHotel;
import com.example.dacn2.entity.hotel.Hotel;
import com.example.dacn2.repository.auth.AccountRepositoryInterface;
import com.example.dacn2.repository.hotel.FavoriteHotelRepository;
import com.example.dacn2.repository.hotel.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteHotelService {

    private final FavoriteHotelRepository favoriteHotelRepository;
    private final HotelRepository hotelRepository;
    private final AccountRepositoryInterface accountRepository;
    private final ViewHistoryService viewHistoryService;

    /**
     * Toggle yêu thích - Safe version
     * 
     * @return null nếu chưa đăng nhập, true nếu thêm, false nếu xóa
     */
    @Transactional
    public Boolean toggleFavoriteSafe(Long accountId, Long hotelId) {
        // Chưa đăng nhập -> return null
        if (accountId == null) {
            return null;
        }

        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            return null;
        }

        if (favoriteHotelRepository.existsByAccountIdAndHotelId(accountId, hotelId)) {
            favoriteHotelRepository.deleteByAccountIdAndHotelId(accountId, hotelId);
            return false; // Đã xóa
        } else {
            Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
            if (hotel == null) {
                return null;
            }

            FavoriteHotel favorite = FavoriteHotel.builder()
                    .account(accountOpt.get())
                    .hotel(hotel)
                    .build();
            favoriteHotelRepository.save(favorite);

            // Ghi nhận vào ViewHistory nếu có lịch sử xem
            viewHistoryService.markClickedFavorite(accountId, hotelId);

            return true; // Đã thêm
        }
    }

    /**
     * Kiểm tra yêu thích - Safe version
     * 
     * @return false nếu chưa đăng nhập hoặc chưa yêu thích
     */
    public boolean isFavoriteSafe(Long accountId, Long hotelId) {
        if (accountId == null) {
            return false;
        }
        return favoriteHotelRepository.existsByAccountIdAndHotelId(accountId, hotelId);
    }

    /**
     * Lấy danh sách yêu thích - Safe version
     * 
     * @return list rỗng nếu chưa đăng nhập
     */
    public List<HotelCardResponse> getFavoriteHotelsSafe(Long accountId) {
        if (accountId == null) {
            return Collections.emptyList();
        }
        List<FavoriteHotel> favorites = favoriteHotelRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
        return favorites.stream()
                .map(fav -> convertToCard(fav.getHotel()))
                .collect(Collectors.toList());
    }

    /**
     * Lấy Set hotelIds đã yêu thích - Safe version
     */
    public Set<Long> getFavoriteHotelIdsSafe(Long accountId) {
        if (accountId == null) {
            return Collections.emptySet();
        }
        return favoriteHotelRepository.findHotelIdsByAccountId(accountId)
                .stream()
                .collect(Collectors.toSet());
    }

    /**
     * Đếm số yêu thích - Safe version
     */
    public long countFavoritesSafe(Long accountId) {
        if (accountId == null) {
            return 0;
        }
        return favoriteHotelRepository.findByAccountIdOrderByCreatedAtDesc(accountId).size();
    }

    private HotelCardResponse convertToCard(Hotel hotel) {
        String thumbnail = null;
        if (hotel.getImages() != null && !hotel.getImages().isEmpty()) {
            thumbnail = hotel.getImages().get(0).getImageUrl();
        }

        Double minPrice = null;
        if (hotel.getRooms() != null && !hotel.getRooms().isEmpty()) {
            minPrice = hotel.getRooms().stream()
                    .filter(r -> r.getPrice() != null)
                    .mapToDouble(r -> r.getPrice())
                    .min()
                    .orElse(0.0);
        }

        return HotelCardResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .address(hotel.getAddress())
                .starRating(hotel.getStarRating())
                .locationName(hotel.getLocation() != null ? hotel.getLocation().getName() : null)
                .thumbnail(thumbnail)
                .minPrice(minPrice)
                .hotelType(hotel.getType() != null ? hotel.getType().name() : null)
                .isFavorite(true)
                .build();
    }
}
