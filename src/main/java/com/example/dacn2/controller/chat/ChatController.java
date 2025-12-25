package com.example.dacn2.controller.chat;

import com.example.dacn2.dto.request.chat.ChatMessageRequest;
import com.example.dacn2.dto.response.chat.ChatMessageResponse;
import com.example.dacn2.dto.response.chat.ChatSessionResponse;
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

    @Autowired
    private com.example.dacn2.service.chat.AssignmentService assignmentService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageRequest request, Principal principal) {
        try {
            if (principal == null) {
                log.error("Principal is null. User not authenticated. Check WebSocket Token.");
                return;
            }
            String senderEmail = principal.getName();

            Account sender = accountRepository.findByEmail(senderEmail)
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
            Long senderId = sender.getId();

            boolean isAdmin = sender.getRoles().stream()
                    .anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("STAFF"));

            Long finalReceiverId = null;

            if (isAdmin) {
                // Nếu là Admin -> Phải gửi đích danh cho User (lấy từ request)
                if (request.getReceiverId() == null) {
                    log.warn("Admin {} gửi tin nhắn nhưng không có receiverId", senderEmail);
                    return;
                }
                finalReceiverId = request.getReceiverId();
            } else {
                // Nếu là Customer -> Tự động tìm Admin để gán
                // Gọi thuật toán Auto-Assign
                Account assignedAdmin = assignmentService.assignUserToAdmin(senderId);

                if (assignedAdmin == null) {
                    // Trường hợp không có Admin nào online
                    // Có thể gửi lại một tin nhắn lỗi cho chính User đó
                    ChatMessageResponse errorMsg = ChatMessageResponse.builder()
                            .content("Hiện tại không có nhân viên hỗ trợ nào trực tuyến. Vui lòng quay lại sau!")
                            .senderName("System")
                            .timestamp(java.time.LocalDateTime.now().toString())
                            .build();

                    messagingTemplate.convertAndSendToUser(
                            senderEmail,
                            "/queue/messages",
                            errorMsg);
                    return;
                }
                finalReceiverId = assignedAdmin.getId();
            }

            // Cập nhật lại receiverId chuẩn vào request để lưu DB
            request.setReceiverId(finalReceiverId);

            log.info("Xử lý tin nhắn: {} -> {}", senderEmail, finalReceiverId);

            // 1. Lưu tin nhắn
            ChatMessageResponse response = chatService.saveMessage(senderId, request);

            // 2. Gửi tin nhắn cho người nhận (Receiver)
            Account receiver = accountRepository.findById(finalReceiverId)
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            messagingTemplate.convertAndSendToUser(
                    receiver.getEmail(),
                    "/queue/messages",
                    response);

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

    /**
     * API cho Admin: Lấy danh sách các cuộc hội thoại gần đây
     */
    @GetMapping("/api/chat/admin/conversations")
    public List<ChatSessionResponse> getAdminConversations() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();

        Account currentUser = accountRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // TODO: Check Role Admin nếu cần thiết

        return chatService.getRecentConversations(currentUser.getId());
    }
}
