package com.example.dacn2.dto.response.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String content;
    private String timestamp; // Trả về dạng String (yyyy-MM-dd HH:mm:ss) cho dễ hiển thị

    private com.example.dacn2.enums.MessageType type;
    private String fileUrl;
    private String fileName;
}
