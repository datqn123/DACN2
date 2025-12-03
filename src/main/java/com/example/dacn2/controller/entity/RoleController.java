package com.example.dacn2.controller.entity;

import com.example.dacn2.dto.request.RoleReuest.AssignPermissionRequest;
import com.example.dacn2.dto.ApiResponse;
import com.example.dacn2.entity.Auth.Role;
import com.example.dacn2.service.UserServiceInterface.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    RoleService roleService;
    @PostMapping("/{roleID}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Role> addPermissionToRole(@PathVariable Long roleID, @RequestBody AssignPermissionRequest request) {
        Role role = roleService.addPermissonsToRole(roleID, request.getPermissionNames());
        return ApiResponse.<Role>builder()
                .result(role)
                .message("Add permissions to role success")
                .build();
    }

    @DeleteMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Role> removePermissions(@PathVariable Long roleId, @RequestBody AssignPermissionRequest request) {

        Role updatedRole = roleService.removePermissionsFromRole(roleId, request.getPermissionNames());

        return ApiResponse.<Role>builder()
                .result(updatedRole)
                .message("Đã gỡ quyền khỏi Role thành công")
                .build();
    }
}
