package com.example.dacn2.service.chat;

import com.example.dacn2.dto.request.chat.ChatMessageRequest;
import com.example.dacn2.dto.response.chat.ChatMessageResponse;
import com.example.dacn2.dto.response.chat.ChatSessionResponse;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.entity.chat.Message;
import com.example.dacn2.repository.AccountRepository;
import com.example.dacn2.repository.chat.MessageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

        private final MessageRepository messageRepository;
        private final AccountRepository accountRepository;

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // lưu tin nhắn vào database
        public ChatMessageResponse saveMessage(Long senderId, ChatMessageRequest request) {
                Account sender = accountRepository.findById(senderId)
                                .orElseThrow(() -> new RuntimeException("Sender not found"));

                Account receiver = accountRepository.findById(request.getReceiverId())
                                .orElseThrow(() -> new RuntimeException("Receiver not found"));

                Message message = Message.builder()
                                .sender(sender)
                                .receiver(receiver)
                                .content(request.getContent())
                                .isRead(false)
                                .type(request.getType() != null ? request.getType()
                                                : com.example.dacn2.enums.MessageType.TEXT)
                                .fileUrl(request.getFileUrl())
                                .fileName(request.getFileName())
                                .build();

                Message savedMessage = messageRepository.save(message);

                return ChatMessageResponse.builder()
                                .id(savedMessage.getId())
                                .senderId(sender.getId())
                                .senderName(sender.getEmail()) // Hoặc lấy full name từ UserProfile
                                .receiverId(receiver.getId())
                                .content(savedMessage.getContent())
                                .timestamp(savedMessage.getTimestamp() != null
                                                ? savedMessage.getTimestamp().format(FORMATTER)
                                                : "")
                                .type(savedMessage.getType())
                                .fileUrl(savedMessage.getFileUrl())
                                .fileName(savedMessage.getFileName())
                                .build();
        }

        // lấy lịch sử tin nhắn giữa 2 người
        public List<ChatMessageResponse> getConversation(Long userId1, Long userId2) {
                List<Message> messages = messageRepository.findConversation(userId1, userId2);

                return messages.stream().map(msg -> ChatMessageResponse.builder()
                                .id(msg.getId())
                                .senderId(msg.getSender().getId())
                                .senderName(msg.getSender().getEmail())
                                .receiverId(msg.getReceiver().getId())
                                .content(msg.getContent())
                                .timestamp(msg.getTimestamp().format(FORMATTER))
                                .type(msg.getType())
                                .fileUrl(msg.getFileUrl())
                                .fileName(msg.getFileName())
                                .build())
                                .collect(Collectors.toList());
        }

        @Transactional
        public void markAsRead(Long receiverId, Long senderId) {
                messageRepository.markAsRead(receiverId, senderId);
        }

        // lấy danh sách tin nhắn của admin
        public List<ChatSessionResponse> getRecentConversations(Long adminId) {
                // 1. Lấy tất cả tin nhắn liên quan đến Admin (Gửi đi hoặc Nhận được)
                List<Message> allMessages = messageRepository.findAllByAccountId(adminId);

                // 2. Group by "Partner" (Người chat cùng)
                // Map<PartnerID, LatestMessage>
                Map<Long, Message> latestMessages = new HashMap<>();

                for (Message msg : allMessages) {
                        Account partner;
                        if (msg.getSender().getId().equals(adminId)) {
                                partner = msg.getReceiver();
                        } else {
                                partner = msg.getSender();
                        }

                        // Logic: Luôn giữ tin nhắn mới nhất
                        if (!latestMessages.containsKey(partner.getId()) ||
                                        msg.getTimestamp()
                                                        .isAfter(latestMessages.get(partner.getId()).getTimestamp())) {
                                latestMessages.put(partner.getId(), msg);
                        }
                }

                // 3. Convert sang DTO
                return latestMessages.values().stream()
                                .sorted((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp())) // Sắp xếp mới nhất
                                                                                                    // lên đầu
                                .map(msg -> {
                                        Account partner = msg.getSender().getId().equals(adminId) ? msg.getReceiver()
                                                        : msg.getSender();

                                        return com.example.dacn2.dto.response.chat.ChatSessionResponse.builder()
                                                        .userId(partner.getId())
                                                        .email(partner.getEmail())
                                                        .fullName(partner.getEmail())
                                                        .lastMessage(msg.getType() == com.example.dacn2.enums.MessageType.IMAGE
                                                                        ? "[Hình ảnh]"
                                                                        : msg.getContent())
                                                        .lastMessageTime(msg.getTimestamp().format(FORMATTER))
                                                        .isRead(msg.isRead())
                                                        .build();
                                })
                                .collect(Collectors.toList());
        }
}
