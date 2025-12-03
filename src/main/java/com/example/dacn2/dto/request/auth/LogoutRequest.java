package com.example.dacn2.dto.request.auth;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}