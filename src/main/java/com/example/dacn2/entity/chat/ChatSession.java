package com.example.dacn2.entity.chat;

import com.example.dacn2.entity.User.Account;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Khách hàng
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Account user;

    // Admin được gán hỗ trợ (có thể null nếu chưa ai nhận)
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Account admin;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SessionStatus status = SessionStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @UpdateTimestamp
    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    public enum SessionStatus {
        ACTIVE, // Đang chat
        CLOSED // Đã kết thúc
    }
}
