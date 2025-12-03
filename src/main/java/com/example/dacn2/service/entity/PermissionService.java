package com.example.dacn2.service.entity;

import com.example.dacn2.dto.request.RoleReuest.PermissionRequest;
import com.example.dacn2.entity.Auth.Permission;
import com.example.dacn2.repository.auth.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {
    @Autowired
    PermissionRepository permissionRepository;

    public List<Permission> getAll() {
        return permissionRepository.findAll();
    }

    public Permission create(PermissionRequest request) {
        if(permissionRepository.existsByName(request.getName())) {
            throw new RuntimeException("Permission is exist");
        }
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        return permissionRepository.save(permission);
    }

    public Permission update(Long id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(id).orElseThrow(()->new RuntimeException("Don't exist permission"));
        permission.setDescription(request.getDescription());
        permission.setName(request.getName());
        return permissionRepository.save(permission);
    }

    public void delete(long id) {
        permissionRepository.deleteById(id);
    }
}
