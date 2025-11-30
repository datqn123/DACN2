package com.example.dacn2.repository;

import com.example.dacn2.entity.Auth.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByName(String name);

    List<Permission> findAllByNameIn(List<String> names);
}
