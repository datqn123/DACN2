package com.example.dacn2.repository.voucher;

import com.example.dacn2.entity.voucher.Voucher;
import com.example.dacn2.entity.voucher.VoucherScope;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
        // Tìm voucher theo mã code (để check khi user nhập)
        Optional<Voucher> findByCode(String code);

        // Kiểm tra trùng code
        boolean existsByCode(String code);

        List<Voucher> findByScope(VoucherScope scope);

        List<Voucher> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(
                        LocalDateTime now1, LocalDateTime now2);

        @Query("SELECT DISTINCT v FROM Voucher v LEFT JOIN v.appliedHotels h " +
                        "WHERE v.isActive = true " +
                        "AND v.usageCount < v.usageLimit " +
                        "AND :now BETWEEN v.startDate AND v.endDate " +
                        "AND v.minOrderValue <= :totalAmount " +
                        "AND (v.scope = 'GLOBAL' OR (v.scope = 'HOTEL_ONLY' AND h.id = :hotelId))")
        List<Voucher> findVoucherForBooking(
                        @Param("now") LocalDateTime now,
                        @Param("totalAmount") Double totalAmount,
                        @Param("hotelId") Long hotelId);

        @Query("SELECT v FROM Voucher v WHERE v.isActive = true ORDER BY v.createdAt DESC")
        List<Voucher> get3VoucherForHome(Pageable pageable);

        @Query("SELECT v FROM Voucher v WHERE v.isActive = true and v.scope = 'HOTEL_ONLY' ORDER BY v.createdAt DESC")
        List<Voucher> get5VoucherForHotelPage(Pageable pageable);

        @Query("SELECT v FROM Voucher v WHERE v.isActive = true and v.scope = 'FLIGHT_ONLY' ORDER BY v.createdAt DESC")
        List<Voucher> get5VoucherForFlightPage(Pageable pageable);
}
