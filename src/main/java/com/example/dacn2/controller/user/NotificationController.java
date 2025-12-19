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

    /**
     * Lấy tất cả notification của user
     * GET /api/notifications
     */
    @GetMapping
    @Operation(summary = "Lấy tất cả thông báo của user đang đăng nhập")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    /**
     * Lấy 10 notification mới nhất (cho dropdown header)
     * GET /api/notifications/recent
     */
    @GetMapping("/recent")
    @Operation(summary = "Lấy 10 thông báo mới nhất")
    public ResponseEntity<List<NotificationResponse>> getRecentNotifications() {
        return ResponseEntity.ok(notificationService.getRecentNotifications());
    }

    /**
     * Đếm số notification chưa đọc (hiển thị badge)
     * GET /api/notifications/unread-count
     */
    @GetMapping("/unread-count")
    @Operation(summary = "Đếm số thông báo chưa đọc")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        Map<String, Long> result = new HashMap<>();
        result.put("count", notificationService.getUnreadCount());
        return ResponseEntity.ok(result);
    }

    /**
     * Đánh dấu 1 notification đã đọc
     * PUT /api/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    @Operation(summary = "Đánh dấu 1 thông báo đã đọc")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);

        Map<String, String> result = new HashMap<>();
        result.put("message", "Đã đánh dấu đọc");
        return ResponseEntity.ok(result);
    }

    /**
     * Đánh dấu tất cả đã đọc
     * PUT /api/notifications/read-all
     */
    @PutMapping("/read-all")
    @Operation(summary = "Đánh dấu tất cả thông báo đã đọc")
    public ResponseEntity<Map<String, String>> markAllAsRead() {
        notificationService.markAllAsRead();

        Map<String, String> result = new HashMap<>();
        result.put("message", "Đã đánh dấu tất cả đã đọc");
        return ResponseEntity.ok(result);
    }
}
