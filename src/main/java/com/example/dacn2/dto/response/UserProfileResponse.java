package com.example.dacn2.dto.response;

import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.User.UserProfile;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private String gender;
    private LocalDate dateOfBirth;
    private String nationality;
    private String address;
    private LocalDateTime createdAt;

    public static UserProfileResponse fromEntity(Account account) {
        UserProfile profile = account.getUserProfile();

        UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .createdAt(account.getCreatedAt());

        if (profile != null) {
            builder.fullName(profile.getFullName())
                    .phoneNumber(profile.getPhoneNumber())
                    .avatarUrl(profile.getAvatarUrl())
                    .gender(profile.getGender())
                    .dateOfBirth(profile.getDateOfBirth())
                    .nationality(profile.getNationality())
                    .address(profile.getAddress());
        }

        return builder.build();
    }
}
