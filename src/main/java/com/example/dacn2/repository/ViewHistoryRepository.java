package com.example.dacn2.repository;

import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.User.ViewHistory;
import com.example.dacn2.entity.hotel.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {

    // Lấy lịch sử xem của user (mới nhất trước)
    List<ViewHistory> findByAccountOrderByViewedAtDesc(Account account);

    Page<ViewHistory> findByAccountOrderByViewedAtDesc(Account account, Pageable pageable);

    // Tìm view gần nhất của user với hotel
    Optional<ViewHistory> findFirstByAccountAndHotelOrderByViewedAtDesc(Account account, Hotel hotel);

    // Kiểm tra user đã xem hotel chưa
    boolean existsByAccountAndHotel(Account account, Hotel hotel);
}
