package com.example.dacn2.dto.request.notification;

import lombok.Data;

@Data
public class NotificationRequest {
    private String title;
    private String message;
    private String link;
    private Boolean isRead = false; // Mặc định là chưa đọc
}
