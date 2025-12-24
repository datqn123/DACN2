package com.example.dacn2.service.chat;

import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.chat.ChatSession;
import com.example.dacn2.repository.AccountRepository;
import com.example.dacn2.repository.chat.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentService {
    private final ChatSessionRepository chatSessionRepository;
    private final PresenceService presenceService;
    private final AccountRepository accountRepository;

    /**
     * Hàm quan trọng nhất: Tìm người phụ trách cho User này
     */
    @Transactional
    public Account assignUserToAdmin(Long userId) {
        // 1. Kiểm tra xem User này đã có phiên chat cũ chưa?
        // Nếu có và trạng thái là ACTIVE -> Trả về Admin cũ luôn (Chat tiếp)
        Optional<ChatSession> existingSession = chatSessionRepository
                .findByUserIdAndStatus(userId, ChatSession.SessionStatus.ACTIVE);
        if (existingSession.isPresent()) {
            Account assignedAdmin = existingSession.get().getAdmin();
            log.info("User {} đã có session active với Admin {}. Tiếp tục...", userId, assignedAdmin.getEmail());

            // Update thời gian hoạt động
            ChatSession session = existingSession.get();
            session.setLastActivityAt(LocalDateTime.now());
            chatSessionRepository.save(session);

            return assignedAdmin;
        }
        // 2. Nếu chưa có -> Tìm Admin mới
        Account bestAdmin = findBestAvailableAdmin();
        if (bestAdmin == null) {
            log.warn("Không tìm thấy Admin nào đang online để hỗ trợ User {}!", userId);
            // Có thể return null hoặc ném Exception tùy logic bạn muốn
            // Ở đây mình return null -> ChatController sẽ xử lý (vd: báo "Hệ thống bận")
            return null;
        }
        // 3. Tạo phiên chat mới (Gán User -> Admin)
        ChatSession newSession = ChatSession.builder()
                .user(accountRepository.findById(userId).orElseThrow())
                .admin(bestAdmin)
                .status(ChatSession.SessionStatus.ACTIVE)
                .startedAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .build();

        chatSessionRepository.save(newSession);
        log.info("Đã gán User {} cho Admin {}", userId, bestAdmin.getEmail());
        return bestAdmin;
    }

    /**
     * Thuật toán tìm Admin "tốt nhất" (Đang online + Ít việc nhất)
     */
    private Account findBestAvailableAdmin() {
        // Lấy danh sách ID các admin đang online từ RAM
        Set<Long> onlineAdminIds = presenceService.getOnlineAdmins();
        log.info("Auto-Assign: Searching for admin. Online Admin IDs: {}", onlineAdminIds);

        if (onlineAdminIds.isEmpty()) {
            return null; // Không ai online
        }
        // Logic: Duyệt qua từng Admin online, xem ai đang gánh ít Session nhất -> Chọn
        Account selectedAdmin = null;
        long minSessions = Long.MAX_VALUE;
        for (Long adminId : onlineAdminIds) {
            // Đếm xem admin này đang chat với bao nhiêu người
            long activeSessions = chatSessionRepository.countActiveSessionsByAdmin(adminId);
            if (activeSessions < minSessions) {
                minSessions = activeSessions;
                // Query lấy thông tin Admin từ DB
                selectedAdmin = accountRepository.findById(adminId).orElse(null);
            }
        }
        return selectedAdmin;
    }

    /**
     * Admin kết thúc phiên chat
     */
    @Transactional
    public void closeSession(Long userId) {
        Optional<ChatSession> session = chatSessionRepository
                .findByUserIdAndStatus(userId, ChatSession.SessionStatus.ACTIVE);

        if (session.isPresent()) {
            ChatSession s = session.get();
            s.setStatus(ChatSession.SessionStatus.CLOSED);
            chatSessionRepository.save(s);
        }
    }
}