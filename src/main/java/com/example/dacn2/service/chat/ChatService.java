package com.example.dacn2.service.chat;

import com.example.dacn2.dto.request.chat.ChatMessageRequest;
import com.example.dacn2.dto.response.chat.ChatMessageResponse;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.chat.Message;
import com.example.dacn2.repository.AccountRepository;
import com.example.dacn2.repository.chat.MessageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Lưu tin nhắn vào Database và convert sang DTO Response
     */
    public ChatMessageResponse saveMessage(Long senderId, ChatMessageRequest request) {
        // 1. Tìm sender & receiver từ DB
        Account sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Account receiver = accountRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // 2. Tạo Entity Message
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .isRead(false)
                .build();

        // 3. Lưu xuống DB (Hibernate tự fill timestamp vì logic @CreationTimestamp)
        Message savedMessage = messageRepository.save(message);

        // 4. Trả về Response DTO để Controller gửi qua WebSocket
        return ChatMessageResponse.builder()
                .id(savedMessage.getId())
                .senderId(sender.getId())
                .senderName(sender.getEmail()) // Hoặc lấy full name từ UserProfile
                .receiverId(receiver.getId())
                .content(savedMessage.getContent())
                .timestamp(savedMessage.getTimestamp() != null ? savedMessage.getTimestamp().format(FORMATTER) : "")
                .build();
    }

    /**
     * Lấy lịch sử chat giữa 2 người
     */
    public List<ChatMessageResponse> getConversation(Long userId1, Long userId2) {
        List<Message> messages = messageRepository.findConversation(userId1, userId2);

        // Convert List<Entity> -> List<DTO>
        return messages.stream().map(msg -> ChatMessageResponse.builder()
                .id(msg.getId())
                .senderId(msg.getSender().getId())
                .senderName(msg.getSender().getEmail())
                .receiverId(msg.getReceiver().getId())
                .content(msg.getContent())
                .timestamp(msg.getTimestamp().format(FORMATTER))
                .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long receiverId, Long senderId) {
        messageRepository.markAsRead(receiverId, senderId);
    }
}
