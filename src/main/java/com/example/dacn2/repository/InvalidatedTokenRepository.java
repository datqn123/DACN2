package com.example.dacn2.repository;

import com.example.dacn2.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    // JpaRepository đã có sẵn hàm existsById(String id) để kiểm tra rồi
}