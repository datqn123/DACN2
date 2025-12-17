package com.example.dacn2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dacn2.entity.User.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    // Kiểm tra user có phải cold start không (true = cold start, false = warm user)
    @Query("SELECT a.coldStart FROM Account a WHERE a.id = :accountId")
    Boolean checkColdStart(@Param("accountId") Long accountId);
}
