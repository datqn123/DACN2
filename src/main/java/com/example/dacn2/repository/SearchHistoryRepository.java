package com.example.dacn2.repository;

import com.example.dacn2.entity.User.SearchHistory;
import com.example.dacn2.entity.User.SearchType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

        // Lấy lịch sử tìm kiếm của user, mới nhất trước
        List<SearchHistory> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

        // Lấy tất cả lịch sử không phân trang
        List<SearchHistory> findByAccountIdOrderByCreatedAtDesc(Long accountId);

        // Kiểm tra đã có lịch sử tìm kiếm này chưa (lấy record mới nhất nếu có
        // duplicate)
        Optional<SearchHistory> findFirstByAccountIdAndKeywordAndSearchTypeOrderByCreatedAtDesc(
                        Long accountId, String keyword, SearchType searchType);

        // Xóa 1 mục lịch sử cụ thể của user
        @Modifying
        @Query("DELETE FROM SearchHistory s WHERE s.account.id = :accountId AND s.id = :id")
        void deleteByAccountIdAndId(@Param("accountId") Long accountId, @Param("id") Long id);

        // Xóa toàn bộ lịch sử của user
        @Modifying
        @Query("DELETE FROM SearchHistory s WHERE s.account.id = :accountId")
        void deleteAllByAccountId(@Param("accountId") Long accountId);

        // Đếm số lượng lịch sử của user
        long countByAccountId(Long accountId);

        // === Queries cho Recommendation System ===

        // Lấy các location user tìm kiếm nhiều nhất
        @Query("SELECT s.location.id, COUNT(s) as cnt FROM SearchHistory s " +
                        "WHERE s.account.id = :accountId AND s.location IS NOT NULL " +
                        "GROUP BY s.location.id ORDER BY cnt DESC")
        List<Object[]> findTopSearchedLocations(@Param("accountId") Long accountId, Pageable pageable);

        // Lấy các loại hotel user hay tìm
        @Query("SELECT s.hotelType, COUNT(s) as cnt FROM SearchHistory s " +
                        "WHERE s.account.id = :accountId AND s.hotelType IS NOT NULL " +
                        "GROUP BY s.hotelType ORDER BY cnt DESC")
        List<Object[]> findTopSearchedHotelTypes(@Param("accountId") Long accountId, Pageable pageable);

        // Lấy khoảng giá user thường tìm (trung bình)
        @Query("SELECT AVG(s.minPrice), AVG(s.maxPrice) FROM SearchHistory s " +
                        "WHERE s.account.id = :accountId AND (s.minPrice IS NOT NULL OR s.maxPrice IS NOT NULL)")
        List<Object[]> findAveragePriceRange(@Param("accountId") Long accountId);

        // Lấy rating user thường filter
        @Query("SELECT s.starRating, COUNT(s) as cnt FROM SearchHistory s " +
                        "WHERE s.account.id = :accountId AND s.starRating IS NOT NULL " +
                        "GROUP BY s.starRating ORDER BY cnt DESC")
        List<Object[]> findTopSearchedStarRatings(@Param("accountId") Long accountId, Pageable pageable);
}
