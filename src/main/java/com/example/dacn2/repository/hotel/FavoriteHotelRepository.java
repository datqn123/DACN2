package com.example.dacn2.repository.hotel;

import com.example.dacn2.entity.hotel.FavoriteHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteHotelRepository extends JpaRepository<FavoriteHotel, Long> {

    // Kiểm tra user đã yêu thích hotel này chưa
    boolean existsByAccountIdAndHotelId(Long accountId, Long hotelId);

    // Tìm favorite cụ thể
    Optional<FavoriteHotel> findByAccountIdAndHotelId(Long accountId, Long hotelId);

    // Lấy tất cả favorites của user
    List<FavoriteHotel> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    // Xóa favorite
    @Modifying
    @Query("DELETE FROM FavoriteHotel f WHERE f.account.id = :accountId AND f.hotel.id = :hotelId")
    void deleteByAccountIdAndHotelId(@Param("accountId") Long accountId, @Param("hotelId") Long hotelId);

    // Lấy danh sách hotelId mà user đã yêu thích (dùng cho check batch)
    @Query("SELECT f.hotel.id FROM FavoriteHotel f WHERE f.account.id = :accountId")
    List<Long> findHotelIdsByAccountId(@Param("accountId") Long accountId);
}
