package com.example.dacn2.entity.notification;

import com.example.dacn2.entity.User.Account;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity lưu trữ thông báo của người dùng
 * Mỗi notification thuộc về 1 user và có thể được đánh dấu đã đọc/chưa đọc
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_user", columnList = "user_id"),
        @Index(name = "idx_notification_user_read", columnList = "user_id, is_read")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người nhận thông báo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Account user;

    // Tiêu đề thông báo (hiển thị bold)
    @Column(nullable = false, length = 200)
    private String title;

    // Nội dung chi tiết
    @Column(nullable = false, length = 500)
    private String message;

    // Loại thông báo (để hiển thị icon/màu khác nhau)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    // Đã đọc chưa
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    // Link điều hướng khi click (ví dụ: /booking/123)
    @Column(length = 255)
    private String link;

    // Thời gian tạo
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
