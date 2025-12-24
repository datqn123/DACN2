package com.example.dacn2.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.dacn2.utils.JWTUtils;

/**
 * Cấu hình WebSocket với STOMP protocol
 * 
 * STOMP = Simple Text Oriented Messaging Protocol
 * - Giống như HTTP nhưng cho messaging
 * - Có khái niệm SUBSCRIBE (đăng ký nhận) và SEND (gửi message)
 */
@Configuration
@EnableWebSocketMessageBroker // Bật WebSocket message broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Cấu hình Message Broker
     * Message Broker = trung gian nhận và phân phối message
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 1. Bật Simple Broker tại các prefix này
        // - /topic = broadcast (gửi cho nhiều người)
        // - /queue = point-to-point (gửi cho 1 người)
        registry.enableSimpleBroker("/topic", "/queue");

        // 2. Prefix cho message từ client gửi lên server
        // VD: client gửi đến /app/chat thì server nhận ở @MessageMapping("/chat")
        registry.setApplicationDestinationPrefixes("/app");

        // 3. Prefix cho message gửi đến user cụ thể
        // VD: /user/123/queue/notifications → gửi cho user có id=123
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * Đăng ký endpoint để client kết nối WebSocket
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint /ws để client handshake
        // withSockJS() = fallback cho browser cũ không hỗ trợ WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Cho phép mọi nguồn (để test 2 máy LAN)
                // (React/Next.js)
                .withSockJS();
    }

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = org.springframework.messaging.support.MessageHeaderAccessor
                        .getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    System.out.println("WEBSOCKET CONNECT: " + authHeader); // DEBUG

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        try {
                            jwtUtils.validateJwtToken(token);
                            String email = jwtUtils.getEmailFromToken(token);
                            System.out.println("WEBSOCKET AUTH SUCCESS: " + email); // DEBUG

                            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                            accessor.setUser(authentication);
                        } catch (Exception e) {
                            System.err.println("WEBSOCKET AUTH FAILED: " + e.getMessage()); // DEBUG
                            e.printStackTrace();
                            return null; // Reject connection
                        }
                    } else {
                        System.err.println("WEBSOCKET NO TOKEN - REJECTING"); // DEBUG
                        return null; // REJECT CONNECTION if no token is present
                    }
                }
                return message;
            }
        });
    }
}
