package com.example.dacn2.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.example.dacn2.service.page.BookingService;

@Component
@Slf4j
public class BookingTimeoutListener extends KeyExpirationEventMessageListener {

    @Autowired
    private BookingService bookingService;

    public BookingTimeoutListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        if (expiredKey.startsWith("booking_timeout:")) {
            try {
                String bookingIdStr = expiredKey.split(":")[1];
                Long bookingId = Long.parseLong(bookingIdStr);

                log.info("⏰ Đơn hàng ID {} đã hết thời gian thanh toán. Đang xử lý...", bookingId);

                bookingService.cancelUnpaidBooking(bookingId);

            } catch (NumberFormatException e) {
                log.error("Lỗi format ID đơn hàng: {}", expiredKey);
            } catch (Exception e) {
                log.error("Lỗi khi hủy đơn hàng tự động: ", e);
            }
        }
    }
}