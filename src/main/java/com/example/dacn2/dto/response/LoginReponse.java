package com.example.dacn2.dto.response;

import lombok.Data;

@Data
public class LoginReponse {
    private Long id;
    private String email;
    private String fullName;
    private String role;
    private String token;
}
