package com.example.dacn2.controller.admin;

import com.example.dacn2.dto.request.RoleReuest.PermissionRequest;
import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.entity.Auth.Permission;
import com.example.dacn2.service.entity.PermissionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/permissions")
public class AdminPermissionController {

    @Autowired
    PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Permission> create(@RequestBody @Valid PermissionRequest request) {
        Permission permission = permissionService.create(request);
        return ApiResponse.<Permission>builder()
                .result(permission)
                .message("Create permission success")
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Permission>> getAll() {
        return ApiResponse.<List<Permission>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Permission> update(@PathVariable Long id, @RequestBody @Valid PermissionRequest request) {
        Permission result = permissionService.update(id, request);
        return ApiResponse.<Permission>builder()
                .result(result)
                .message("Cập nhật quyền thành công")
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Xóa quyền thành công")
                .build();
    }

}
