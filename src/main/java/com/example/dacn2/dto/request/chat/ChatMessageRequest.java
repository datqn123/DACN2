package com.example.dacn2.dto.request.chat;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private Long receiverId; // ID người nhận (Admin nhận tin từ ID user, User nhận tin từ ID admin)
    private String content; // Nội dung tin nhắn

    // Multimedia extensions
    private com.example.dacn2.enums.MessageType type;
    private String fileUrl;
    private String fileName;
}
