package com.example.dacn2.dto.request;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}