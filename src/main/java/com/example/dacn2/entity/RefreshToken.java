package com.example.dacn2.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token; // Chuỗi token ngẫu nhiên (UUID)

    @Column(nullable = false)
    private Instant expiryDate; // Thời điểm hết hạn

    // Một Account có thể có 1 (hoặc nhiều) Refresh Token
    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;
}