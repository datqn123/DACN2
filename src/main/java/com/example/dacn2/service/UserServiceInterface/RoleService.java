package com.example.dacn2.service.UserServiceInterface;

import com.example.dacn2.entity.Permission;
import com.example.dacn2.entity.Role;
import com.example.dacn2.repository.PermissionRepository;
import com.example.dacn2.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PermissionRepository permissionRepository;
    public Role addPermissonsToRole(long idRole, List<String> permissionNames) {
        Role role = roleRepository.findById(idRole).orElseThrow(() -> new RuntimeException("Role not exist"));
        List<Permission> permissionsToAdd = permissionRepository.findAllByNameIn(permissionNames);
        if(permissionsToAdd.isEmpty()) {
            throw new RuntimeException("Don't have any permission");
        }
        role.getPermissions().addAll(permissionsToAdd);
        return roleRepository.save(role);
    }

    public Role removePermissionsFromRole(Long roleId, List<String> permissionNames) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));

        List<Permission> permissionsToRemove = permissionRepository.findAllByNameIn(permissionNames);

        // Xóa khỏi Set
        role.getPermissions().removeAll(permissionsToRemove);

        return roleRepository.save(role);
    }
}
