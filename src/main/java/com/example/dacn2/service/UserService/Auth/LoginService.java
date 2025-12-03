package com.example.dacn2.service.UserService.Auth;

import com.example.dacn2.dto.request.auth.LoginRequest;
import com.example.dacn2.dto.request.auth.RegisterRequest;
import com.example.dacn2.dto.response.LoginReponse;
import com.example.dacn2.entity.Auth.InvalidatedToken;
import com.example.dacn2.entity.Auth.RefreshToken;
import com.example.dacn2.entity.Auth.Role;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.User.UserProfile;
import com.example.dacn2.repository.auth.AccountRepositoryInterface;
import com.example.dacn2.repository.auth.InvalidatedTokenRepository;
import com.example.dacn2.repository.auth.RefreshTokenRepository;
import com.example.dacn2.repository.auth.RoleRepository;
import com.example.dacn2.utils.JWTUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class LoginService {
    @Autowired
    AccountRepositoryInterface accountRepositoryInterface;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JWTUtils jwtUtils;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    InvalidatedTokenRepository invalidatedTokenRepository;

    @Transactional
    public LoginReponse login(LoginRequest request) {
        String cleanEmail = request.getEmail().trim();
        Account account = accountRepositoryInterface.findByEmail(cleanEmail)
                .orElseThrow(() -> {
                    // In ra log nếu không tìm thấy
                    System.out.println("LỖI: Không tìm thấy email [" + cleanEmail + "] trong Database!");
                    return new RuntimeException("Email not exist");
                });
        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new RuntimeException("Mat khau khong dung");
        }
        if ("BANNED".equals(account.getStatus())) {
            throw new RuntimeException("Tai khoan da bi cam");
        }
        String token = jwtUtils.generaToken(account);
        // create refreshToken
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(account.getId());
        LoginReponse response = new LoginReponse();
        response.setId(account.getId());
        response.setEmail(account.getEmail());
        response.setAccesToken(token);
        response.setRefreshToken(refreshToken.getToken());
        response.setRoles(account.getRoles());

        return response;
    }

    public Account registerAccount(RegisterRequest request) {
        if(!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu koong khop");
        }
        if(accountRepositoryInterface.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Tai khoan da ton tai");
        }
        Role userRole = roleRepository.findByName("USER").orElseThrow( () -> new RuntimeException("Không tìm thấy Role") );
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRoles(roles);
        account.setStatus("ACTIVE");

        UserProfile userProfile = new UserProfile();
        userProfile.setFullName(request.getFullName());
        userProfile.setPhoneNumber(request.getPhoneNumber());

        account.setUserProfile(userProfile);
        userProfile.setAccount(account);

        return accountRepositoryInterface.save(account);
    }

    @Transactional
    public void logout(String refreshToken, String accessToken) {
        // 1. Xóa Refresh Token (Logic cũ)
        refreshTokenService.findByToken(refreshToken);
        refreshTokenRepository.deleteByToken(refreshToken);

        // 2. Đưa Access Token vào Blacklist (Logic Mới)
        try {
            // Lấy thời gian hết hạn của Access Token từ JwtUtils
            // (Bạn cần chắc chắn JwtUtils có hàm getExpirationFromToken, xem Bước 3b bên dưới)
            Date expiryDate = jwtUtils.getExpirationFromToken(accessToken);

            String tokenId = accessToken; // Dùng luôn chuỗi token làm ID

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(tokenId)
                    .expiryTime(expiryDate)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (Exception e) {
            // Token lỗi hoặc đã hết hạn thì bỏ qua, không cần blacklist nữa
            System.out.println("Token không hợp lệ để blacklist: " + e.getMessage());
        }
    }
}
