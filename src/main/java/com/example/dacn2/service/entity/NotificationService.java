package com.example.dacn2.service.entity;

import com.example.dacn2.dto.response.NotificationResponse;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.notification.Notification;
import com.example.dacn2.entity.notification.NotificationType;
import com.example.dacn2.repository.AccountRepository;
import com.example.dacn2.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý logic Notification
 * - Tạo và lưu notification vào DB
 * - Gửi realtime qua WebSocket
 * - Đánh dấu đã đọc
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    // SimpMessagingTemplate = công cụ để gửi message qua WebSocket
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi notification đến user (lưu DB + gửi realtime)
     * 
     * @param userId  ID người nhận
     * @param title   Tiêu đề (VD: "Đặt phòng thành công!")
     * @param message Nội dung chi tiết
     * @param type    Loại notification
     * @param link    Link điều hướng khi click (có thể null)
     */
    public void sendNotification(Long userId, String title, String message,
            NotificationType type, String link) {
        // 1. Tìm user
        Account user = accountRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            log.warn("Cannot send notification: User {} not found", userId);
            return;
        }

        // 2. Tạo và lưu notification vào DB
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .link(link)
                .isRead(false)
                .build();

        notification = notificationRepository.save(notification);
        log.info("📬 Saved notification #{} for user {}", notification.getId(), userId);

        // 3. Gửi realtime qua WebSocket
        // Destination: /user/{userId}/queue/notifications
        NotificationResponse response = NotificationResponse.fromEntity(notification);
        messagingTemplate.convertAndSendToUser(
                userId.toString(), // User ID
                "/queue/notifications", // Destination (Spring tự thêm /user/{userId} phía trước)
                response // Payload
        );
        log.info("🔔 Sent realtime notification to user {}", userId);
    }

    /**
     * Lấy danh sách notification của user đang đăng nhập
     */
    public List<NotificationResponse> getMyNotifications() {
        Long userId = getCurrentUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy top 10 notification mới nhất (cho dropdown)
     */
    public List<NotificationResponse> getRecentNotifications() {
        Long userId = getCurrentUserId();
        return notificationRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Đếm số notification chưa đọc
     */
    public Long getUnreadCount() {
        Long userId = getCurrentUserId();
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * Đánh dấu 1 notification đã đọc
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification không tồn tại"));

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Đánh dấu tất cả đã đọc
     */
    @Transactional
    public void markAllAsRead() {
        Long userId = getCurrentUserId();
        int count = notificationRepository.markAllAsReadByUserId(userId);
        log.info("Marked {} notifications as read for user {}", count, userId);
    }

    /**
     * Lấy userId từ Security Context
     */
    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        return user.getId();
    }
}
