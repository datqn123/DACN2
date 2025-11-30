package com.example.dacn2.dto.response;

import com.example.dacn2.entity.Auth.Role;
import lombok.Data;

import java.util.Set;

@Data
public class LoginReponse {
    private Long id;
    private String email;
    private String fullName;
    private Set<Role> roles;
    private String accesToken;
    private String refreshToken;
}
