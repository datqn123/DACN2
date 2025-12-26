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

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Long userId, String title, String message,
            NotificationType type, String link) {
        Account user = accountRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            log.warn("Cannot send notification: User {} not found", userId);
            return;
        }

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

        NotificationResponse response = NotificationResponse.fromEntity(notification);
        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/notifications",
                response);
    }

    public void sendPublicNotification(String title, String message, String link) {
        NotificationResponse response = NotificationResponse.builder()
                .title(title)
                .message(message)
                .type(NotificationType.SYSTEM)
                .link(link)
                .isRead(false)
                .build();

        messagingTemplate.convertAndSend("/topic/public/notifications", response);
    }

    public List<NotificationResponse> getMyNotifications() {
        Long userId = getCurrentUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> getRecentNotifications() {
        Long userId = getCurrentUserId();
        return notificationRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Long getUnreadCount() {
        Long userId = getCurrentUserId();
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification không tồn tại"));

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead() {
        Long userId = getCurrentUserId();
        int count = notificationRepository.markAllAsReadByUserId(userId);
        log.info("Marked {} notifications as read for user {}", count, userId);
    }

    @Transactional
    public Notification saveFromClient(String title, String message, String link, Boolean isRead) {
        Long userId = getCurrentUserId();
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(NotificationType.SYSTEM)
                .link(link)
                .isRead(Boolean.TRUE.equals(isRead))
                .build();

        return notificationRepository.save(notification);
    }

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        return user.getId();
    }
}
