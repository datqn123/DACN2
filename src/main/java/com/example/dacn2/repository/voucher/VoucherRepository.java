package com.example.dacn2.repository.voucher;

import com.example.dacn2.entity.voucher.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    // Tìm voucher theo mã code (để check khi user nhập)
    Optional<Voucher> findByCode(String code);

    // Kiểm tra trùng code
    boolean existsByCode(String code);
}
