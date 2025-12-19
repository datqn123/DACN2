package com.example.dacn2.dto.response;

import com.example.dacn2.entity.notification.Notification;
import com.example.dacn2.entity.notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO trả về thông báo cho frontend
 * Không expose trực tiếp Entity để bảo mật và linh hoạt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private String link;
    private LocalDateTime createdAt;

    // Thêm field để hiển thị thời gian dạng "5 phút trước"
    private String timeAgo;

    /**
     * Convert từ Entity sang DTO
     */
    public static NotificationResponse fromEntity(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .link(notification.getLink())
                .createdAt(notification.getCreatedAt())
                .timeAgo(calculateTimeAgo(notification.getCreatedAt()))
                .build();
    }

    /**
     * Tính thời gian dạng "X phút trước", "X giờ trước"
     */
    private static String calculateTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null)
            return "";

        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createdAt, now).toMinutes();

        if (minutes < 1)
            return "Vừa xong";
        if (minutes < 60)
            return minutes + " phút trước";

        long hours = minutes / 60;
        if (hours < 24)
            return hours + " giờ trước";

        long days = hours / 24;
        if (days < 7)
            return days + " ngày trước";

        if (days < 30)
            return (days / 7) + " tuần trước";

        return createdAt.toLocalDate().toString();
    }
}
