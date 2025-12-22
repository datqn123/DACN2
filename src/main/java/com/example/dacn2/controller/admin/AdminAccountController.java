package com.example.dacn2.controller.admin;

import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.dto.response.admin.AdminUserResponse;
import com.example.dacn2.service.Admin.AdminAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountController {

    private final AdminAccountService adminAccountService;

    @GetMapping
    public ApiResponse<List<AdminUserResponse>> getAll() {
        return ApiResponse.<List<AdminUserResponse>>builder()
                .result(adminAccountService.getAllAccounts())
                .message("Lấy danh sách tài khoản thành công")
                .build();
    }

    @PutMapping("/{id}/status")
    public ApiResponse<AdminUserResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ApiResponse.<AdminUserResponse>builder()
                .result(adminAccountService.updateStatus(id, status))
                .message("Cập nhật trạng thái tài khoản thành công")
                .build();
    }

    @PutMapping("/{id}/role")
    public ApiResponse<AdminUserResponse> updateRole(
            @PathVariable Long id,
            @RequestParam String role) {
        return ApiResponse.<AdminUserResponse>builder()
                .result(adminAccountService.updateRole(id, role))
                .message("Cập nhật quyền tài khoản thành công")
                .build();
    }
}
