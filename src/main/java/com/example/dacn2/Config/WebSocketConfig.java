package com.example.dacn2.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

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
                .setAllowedOriginPatterns("*") // Cho phép mọi origin (CORS)
                .withSockJS(); // Hỗ trợ SockJS fallback

        // Endpoint không có SockJS (cho native WebSocket client)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }
}
