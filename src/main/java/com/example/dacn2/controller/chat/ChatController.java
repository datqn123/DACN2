package com.example.dacn2.controller.chat;

import com.example.dacn2.dto.request.chat.ChatMessageRequest;
import com.example.dacn2.dto.response.chat.ChatMessageResponse;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.repository.AccountRepository;
import com.example.dacn2.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @Autowired
    private AccountRepository accountRepository;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageRequest request, Principal principal) {
        try {
            String senderEmail = principal.getName();
            log.info("Nhận tin nhắn từ: {} gửi tới ID: {}", senderEmail, request.getReceiverId());

            Account sender = accountRepository.findByEmail(senderEmail)
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
            Long senderId = sender.getId();

            // 1. Lưu tin nhắn
            ChatMessageResponse response = chatService.saveMessage(senderId, request);

            // 2. Gửi tin nhắn cho người nhận (Receiver)
            // CẦN GỬI THEO EMAIL, vì Principal của Socket là Email
            Account receiver = accountRepository.findById(request.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            messagingTemplate.convertAndSendToUser(
                    receiver.getEmail(),
                    "/queue/messages",
                    response);

            // 3. Gửi lại cho người gửi (để hiển thị real-time trên UI của họ nếu cần, hoặc
            // họ tự append)
            // messagingTemplate.convertAndSendToUser(
            // String.valueOf(senderId),
            // "/queue/messages",
            // response
            // );

        } catch (Exception e) {
            log.error("Lỗi gửi tin nhắn: ", e);
        }
    }

    /**
     * REST API: Lấy lịch sử chat
     * GET /api/messages/{userId}
     */
    @GetMapping("/api/messages/{targetUserId}")
    public List<ChatMessageResponse> getChatHistory(@PathVariable Long targetUserId) {
        // Lấy ID người đang đăng nhập
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();

        Account currentUser = accountRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        Long currentUserId = currentUser.getId();

        return chatService.getConversation(currentUserId, targetUserId);
    }

    @PutMapping("/api/messages/mark-read/{senderId}")
    public void markAsRead(@PathVariable Long senderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();

        Account currentUser = accountRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        chatService.markAsRead(currentUser.getId(), senderId);
    }
}
