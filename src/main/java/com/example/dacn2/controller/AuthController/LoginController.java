package com.example.dacn2.controller.AuthController;

import com.example.dacn2.dto.request.LoginRequest;
import com.example.dacn2.dto.response.ApiResponse;
import com.example.dacn2.dto.response.LoginReponse;
import com.example.dacn2.service.UserService.Auth.LoginService;
import com.example.dacn2.service.UserServiceInterface.Auth.LoginServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Autowired
    private LoginServiceInterface loginService;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest request) {
        LoginReponse loginReponse = loginService.login(request);
        return ApiResponse.<LoginReponse>builder()
                .result(loginReponse)
                .message("Đăng nhập thành công")
                .build();
    }
}
