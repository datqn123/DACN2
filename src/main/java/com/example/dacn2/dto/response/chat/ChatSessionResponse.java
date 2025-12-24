package com.example.dacn2.dto.response.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatSessionResponse {
    private Long userId; // ID của người chat với Admin (Customer)
    private String email; // Email của khách
    private String fullName; // Tên của khách (nếu có)
    private String avatar; // Avatar (nếu có)
    private String lastMessage; // Nội dung tin nhắn cuối cùng
    private String lastMessageTime; // Thời gian tin nhắn cuối
    private boolean isRead; // Trạng thái đã đọc hay chưa (của tin cuối)
}
