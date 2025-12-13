package com.example.dacn2.controller.public_api;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.request.UpdateProfileRequest;
import com.example.dacn2.dto.response.UserProfileResponse;
import com.example.dacn2.service.entity.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/user")
@PreAuthorize("hasRole('USER')")
public class UserController {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Lấy thông tin profile của user đang đăng nhập
     */
    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getMyProfile() {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getMyProfile())
                .message("Lấy thông tin profile thành công")
                .build();
    }

    /**
     * Cập nhật thông tin profile
     */
    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateMyProfile(@RequestBody @Valid UpdateProfileRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateMyProfile(request))
                .message("Cập nhật profile thành công")
                .build();
    }
}
