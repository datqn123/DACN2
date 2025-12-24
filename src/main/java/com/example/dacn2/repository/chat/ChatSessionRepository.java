package com.example.dacn2.repository.chat;

import com.example.dacn2.entity.chat.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    // Tìm session xem user này có đang chat dở với ai không (Status = ACTIVE)
    Optional<ChatSession> findByUserIdAndStatus(Long userId, ChatSession.SessionStatus status);

    // Đếm số session ACTIVE mà admin này đang phụ trách (để cân bằng tải)
    @Query("SELECT COUNT(cs) FROM ChatSession cs WHERE cs.admin.id = :adminId AND cs.status = 'ACTIVE'")
    long countActiveSessionsByAdmin(Long adminId);

    // Tìm tất cả session active của 1 admin
    List<ChatSession> findByAdminIdAndStatus(Long adminId, ChatSession.SessionStatus status);
}
