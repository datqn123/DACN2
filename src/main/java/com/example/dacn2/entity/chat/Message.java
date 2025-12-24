package com.example.dacn2.entity.chat;

import com.example.dacn2.entity.User.Account;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Account receiver;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "is_read", columnDefinition = "boolean default false")
    private boolean isRead = false;
}
