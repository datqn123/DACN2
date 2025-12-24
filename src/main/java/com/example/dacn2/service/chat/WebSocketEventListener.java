package com.example.dacn2.service.chat;

import com.example.dacn2.entity.Auth.Role;
import com.example.dacn2.entity.User.Account;
import com.example.dacn2.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final AccountRepository accountRepository;
    private final PresenceService presenceService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // Lấy thông tin user từ session
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = headerAccessor.getUser();

        if (principal != null) {
            String email = principal.getName();
            Optional<Account> userOpt = accountRepository.findByEmail(email);

            if (userOpt.isPresent()) {
                Account user = userOpt.get();
                // Log roles để debug
                log.info("WebSocket Connect: User {} - Roles: {}", email, user.getRoles());

                // Kiểm tra xem có phải ADMIN hoặc STAFF không
                boolean isAdmin = user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMIN") || role.getName().equals("STAFF")); // Check
                                                                                                             // both
                                                                                                             // cases
                                                                                                             // just to
                                                                                                             // be safe

                if (isAdmin) {
                    presenceService.addAdmin(user.getId());
                    log.info("User {} identified as ADMIN/STAFF. Added to PresenceService.", email);
                } else {
                    log.info("User {} is NOT identified as ADMIN/STAFF. Ignored by PresenceService.", email);
                }
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = headerAccessor.getUser();

        if (principal != null) {
            String email = principal.getName();
            // Query lại DB để lấy ID (Hoặc tối ưu bằng cách lưu ID vào session attributes
            // lúc connect)
            Optional<Account> userOpt = accountRepository.findByEmail(email);

            if (userOpt.isPresent()) {
                Account user = userOpt.get();
                boolean isAdmin = user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMIN") || role.getName().equals("STAFF"));

                if (isAdmin) {
                    presenceService.removeAdmin(user.getId());
                }
            }
        }
    }
}
