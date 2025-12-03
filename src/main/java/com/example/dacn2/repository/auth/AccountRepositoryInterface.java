package com.example.dacn2.repository.auth;

import com.example.dacn2.entity.User.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepositoryInterface extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
}
