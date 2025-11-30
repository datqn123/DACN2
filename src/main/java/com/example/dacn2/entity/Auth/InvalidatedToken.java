package com.example.dacn2.entity.Auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "invalidated_tokens")
public class InvalidatedToken {
    @Id
    private String id; // Lưu chính cái chuỗi Access Token vào đây

    private Date expiryTime; // Thời điểm token này thực sự hết hạn
}
