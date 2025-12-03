package com.example.dacn2.service.UserService.Auth;

import com.example.dacn2.dto.request.RefreshTokenRequest;
import com.example.dacn2.dto.response.LoginReponse;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.Auth.RefreshToken;
import com.example.dacn2.repository.auth.AccountRepositoryInterface;
import com.example.dacn2.repository.auth.RefreshTokenRepository;
import com.example.dacn2.utils.JWTUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenDurationMs;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    AccountRepositoryInterface accountRepositoryInterface;
    @Autowired
    JWTUtils jwtUtils;
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Refresh Token not exist"));
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        // delete old refresh token when user login
        accountRepositoryInterface.findById(userId).ifPresent(account -> refreshTokenRepository.deleteByAccount(account));
        refreshTokenRepository.flush();
        Account account = accountRepositoryInterface.findById(userId).get();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAccount(account);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token); // Xóa khỏi DB
            throw new RuntimeException("Refresh token đã hết hạn. Vui lòng đăng nhập lại!");
        }
        return token;
    }

    public LoginReponse refreshToken(RefreshTokenRequest request) {
        String tokenRefresh = request.getRefreshToken();
        RefreshToken token = this.findByToken(tokenRefresh);

        this.verifyExpiration(token);

        Account account = token.getAccount();
        String newAccessToken = jwtUtils.generaToken(account);

        LoginReponse reponse = new LoginReponse();
        reponse.setAccesToken(newAccessToken);
        reponse.setRefreshToken(tokenRefresh);
        return reponse;
    }
}
