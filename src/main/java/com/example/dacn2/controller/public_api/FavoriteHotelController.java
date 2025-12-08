package com.example.dacn2.controller.public_api;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.response.home.HotelCardResponse;
import com.example.dacn2.service.entity.FavoriteHotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public/favorites/hotels")
@RequiredArgsConstructor
public class FavoriteHotelController {

        private final FavoriteHotelService favoriteHotelService;
        private final com.example.dacn2.repository.auth.AccountRepositoryInterface accountRepository;

        @PostMapping("/{hotelId}")
        public ApiResponse<Map<String, Object>> toggleFavorite(
                        @PathVariable Long hotelId,
                        @AuthenticationPrincipal UserDetails userDetails) {

                Long accountId = getAccountId(userDetails);
                Boolean result = favoriteHotelService.toggleFavoriteSafe(accountId, hotelId);

                if (result == null) {
                        return ApiResponse.<Map<String, Object>>builder()
                                        .result(Map.of("hotelId", hotelId, "isFavorite", false))
                                        .message("Vui lòng đăng nhập để yêu thích")
                                        .build();
                }

                return ApiResponse.<Map<String, Object>>builder()
                                .result(Map.of("hotelId", hotelId, "isFavorite", result))
                                .message(result ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích")
                                .build();
        }

        @GetMapping("/{hotelId}/check")
        public ApiResponse<Map<String, Object>> checkFavorite(
                        @PathVariable Long hotelId,
                        @AuthenticationPrincipal UserDetails userDetails) {

                Long accountId = getAccountId(userDetails);
                boolean isFavorite = favoriteHotelService.isFavoriteSafe(accountId, hotelId);

                return ApiResponse.<Map<String, Object>>builder()
                                .result(Map.of("hotelId", hotelId, "isFavorite", isFavorite))
                                .build();
        }

        @GetMapping
        public ApiResponse<List<HotelCardResponse>> getFavoriteHotels(
                        @AuthenticationPrincipal UserDetails userDetails) {

                Long accountId = getAccountId(userDetails);
                List<HotelCardResponse> favorites = favoriteHotelService.getFavoriteHotelsSafe(accountId);

                return ApiResponse.<List<HotelCardResponse>>builder()
                                .result(favorites)
                                .message(accountId != null ? "Lấy danh sách thành công" : "Vui lòng đăng nhập")
                                .build();
        }

        @GetMapping("/count")
        public ApiResponse<Map<String, Long>> countFavorites(
                        @AuthenticationPrincipal UserDetails userDetails) {

                Long accountId = getAccountId(userDetails);
                long count = favoriteHotelService.countFavoritesSafe(accountId);

                return ApiResponse.<Map<String, Long>>builder()
                                .result(Map.of("count", count))
                                .build();
        }

        private Long getAccountId(UserDetails userDetails) {
                if (userDetails == null)
                        return null;
                return accountRepository.findByEmail(userDetails.getUsername())
                                .map(account -> account.getId())
                                .orElse(null);
        }
}
