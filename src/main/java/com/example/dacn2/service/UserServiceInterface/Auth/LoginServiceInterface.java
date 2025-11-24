package com.example.dacn2.service.UserServiceInterface.Auth;

import com.example.dacn2.dto.request.LoginRequest;
import com.example.dacn2.dto.response.LoginReponse;

public interface LoginServiceInterface {
    public LoginReponse login(LoginRequest request);
}
