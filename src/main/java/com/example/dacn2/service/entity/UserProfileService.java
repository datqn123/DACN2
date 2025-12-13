package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.UpdateProfileRequest;
import com.example.dacn2.dto.response.UserProfileResponse;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.User.UserProfile;
import com.example.dacn2.repository.AccountRepository;
import com.example.dacn2.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Lấy thông tin profile của user đang đăng nhập
     */
    public UserProfileResponse getMyProfile() {
        Account user = getCurrentUser();
        return UserProfileResponse.fromEntity(user);
    }

    /**
     * Cập nhật thông tin profile của user đang đăng nhập
     */
    @Transactional
    public UserProfileResponse updateMyProfile(UpdateProfileRequest request) {
        Account user = getCurrentUser();
        UserProfile profile = user.getUserProfile();

        // Nếu chưa có profile thì tạo mới
        if (profile == null) {
            profile = new UserProfile();
            profile.setAccount(user);
            user.setUserProfile(profile);
        }

        // Cập nhật các field nếu có giá trị mới
        if (request.getFullName() != null) {
            profile.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }
        if (request.getDateOfBirth() != null) {
            profile.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getNationality() != null) {
            profile.setNationality(request.getNationality());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (request.getAvatarUrl() != null) {
            profile.setAvatarUrl(request.getAvatarUrl());
        }

        userProfileRepository.save(profile);
        return UserProfileResponse.fromEntity(user);
    }

    /**
     * Lấy user đang đăng nhập từ SecurityContext
     */
    private Account getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }
}
