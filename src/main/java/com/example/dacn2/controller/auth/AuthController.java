package com.example.dacn2.controller.auth;

import com.example.dacn2.dto.request.auth.LoginRequest;
import com.example.dacn2.dto.request.auth.LogoutRequest;
import com.example.dacn2.dto.request.RefreshTokenRequest;
import com.example.dacn2.dto.request.auth.RegisterRequest;
import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.response.LoginReponse;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.service.Auth.LoginService;
import com.example.dacn2.service.Auth.RefreshTokenService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private LoginService loginService;
    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody @Valid LoginRequest request) {
        LoginReponse loginReponse = loginService.login(request);
        return ApiResponse.<LoginReponse>builder()
                .result(loginReponse)
                .message("Đăng nhập thành công")
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<Account> register(@RequestBody @Valid RegisterRequest request) {

        Account newAccount = loginService.registerAccount(request);

        return ApiResponse.<Account>builder()
                .code(1000)
                .message("Đăng ký tài khoản thành công")
                .result(newAccount)
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<LoginReponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        LoginReponse result = refreshTokenService.refreshToken(request);
        return ApiResponse.<LoginReponse>builder()
                .result(result)
                .message("Success refresh token")
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestBody LogoutRequest request,
            @RequestHeader("Authorization") String authHeader) {
        // Cắt bỏ chữ "Bearer " để lấy chuỗi Access Token trần
        String accessToken = "";
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }

        // Gọi Service xử lý (Xóa Refresh + Blacklist Access)
        loginService.logout(request.getRefreshToken(), accessToken);

        return ApiResponse.<Void>builder()
                .message("Đăng xuất thành công")
                .build();
    }

}
