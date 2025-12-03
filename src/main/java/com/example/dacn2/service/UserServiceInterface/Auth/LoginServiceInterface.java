package com.example.dacn2.service.UserServiceInterface.Auth;

import com.example.dacn2.dto.request.auth.LoginRequest;
import com.example.dacn2.dto.request.auth.RegisterRequest;
import com.example.dacn2.dto.response.LoginReponse;
import com.example.dacn2.entity.User.Account;

public interface LoginServiceInterface {
    public LoginReponse login(LoginRequest request);
    Account registerAccount(RegisterRequest request);
    public void logout(String refreshToken, String accessToken);
}
