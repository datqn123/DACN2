package com.example.dacn2.dto.response.admin;

import com.example.dacn2.entity.Auth.Role;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.User.UserProfile;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class AdminUserResponse {
    // DTO for Admin User Management
    private Long id;

    private String email;
    private String fullName;
    private String phoneNumber;
    private String status;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AdminUserResponse fromEntity(Account account) {
        UserProfile profile = account.getUserProfile();

        return AdminUserResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .roles(account.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                // Map fields from UserProfile if exists
                .fullName(profile != null ? profile.getFullName() : null)
                .phoneNumber(profile != null ? profile.getPhoneNumber() : null)
                .build();
    }
}
