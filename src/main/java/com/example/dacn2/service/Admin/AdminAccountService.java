package com.example.dacn2.service.Admin;

import com.example.dacn2.dto.response.admin.AdminUserResponse;
import com.example.dacn2.entity.Auth.Role;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.repository.AccountRepository;
import com.example.dacn2.repository.auth.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    /**
     * Lấy danh sách tất cả user
     */
    public List<AdminUserResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AdminUserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật trạng thái (Lock/Unlock)
     */
    @Transactional
    public AdminUserResponse updateStatus(Long id, String status) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản: " + id));

        account.setStatus(status); // ACTIVE / LOCKED
        return AdminUserResponse.fromEntity(accountRepository.save(account));
    }

    /**
     * Cập nhật Role cho user
     */
    @Transactional
    public AdminUserResponse updateRole(Long id, String roleName) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản: " + id));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role không tồn tại: " + roleName));

        // Logic thay thế toàn bộ role cũ bằng role mới (Single Role model)
        // Nếu muốn Multi-role thì cần logic khác (add/remove)
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        account.setRoles(roles);

        return AdminUserResponse.fromEntity(accountRepository.save(account));
    }
}
