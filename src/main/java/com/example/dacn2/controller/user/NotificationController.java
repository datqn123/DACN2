package com.example.dacn2.controller.user;

import com.example.dacn2.dto.response.NotificationResponse;
import com.example.dacn2.service.entity.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller cho Notification
 * Tất cả API yêu cầu đăng nhập (authenticated)
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "API quản lý thông báo")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Lưu thông báo từ client")
    public ResponseEntity<NotificationResponse> create(
            @RequestBody com.example.dacn2.dto.request.notification.NotificationRequest request) {

        var notification = notificationService.saveFromClient(
                request.getTitle(),
                request.getMessage(),
                request.getLink(),
                request.getIsRead());

        return ResponseEntity.ok(NotificationResponse.fromEntity(notification));
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả thông báo của user đang đăng nhập")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    @GetMapping("/recent")
    @Operation(summary = "Lấy 10 thông báo mới nhất")
    public ResponseEntity<List<NotificationResponse>> getRecentNotifications() {
        return ResponseEntity.ok(notificationService.getRecentNotifications());
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Đếm số thông báo chưa đọc")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        Map<String, Long> result = new HashMap<>();
        result.put("count", notificationService.getUnreadCount());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Đánh dấu 1 thông báo đã đọc")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);

        Map<String, String> result = new HashMap<>();
        result.put("message", "Đã đánh dấu đọc");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/read-all")
    @Operation(summary = "Đánh dấu tất cả thông báo đã đọc")
    public ResponseEntity<Map<String, String>> markAllAsRead() {
        notificationService.markAllAsRead();

        Map<String, String> result = new HashMap<>();
        result.put("message", "Đã đánh dấu tất cả đã đọc");
        return ResponseEntity.ok(result);
    }
}
