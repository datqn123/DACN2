package com.example.dacn2.repository.auth;

import com.example.dacn2.entity.Auth.RefreshToken;
import com.example.dacn2.entity.User.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    // Hàm xóa token của user (dùng khi logout hoặc login lại)
    @Modifying
    int deleteByAccount(Account account);

    @Modifying
    void deleteByToken(String token);
}