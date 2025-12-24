package com.example.dacn2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.dacn2.dto.request.email.EmailRequest;
import com.example.dacn2.entity.booking.Booking;
import com.example.dacn2.repository.BookingRepository;
import com.example.dacn2.service.page.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumerService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EmailService emailService;
    @Autowired
    private BookingRepository bookingRepository;

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "MY_TOPIC", groupId = "group_id")
    public void consume(String message) {
        System.out.println(String.format("#### -> Consumed message -> %s", message));
    }

    @KafkaListener(topics = "email_topic", groupId = "group_id")
    public void consumeEmail(String message) {
        System.out.println(String.format("#### -> Consumed email -> %s", message));
        try {
            EmailRequest emailRequest = objectMapper.readValue(message, EmailRequest.class);
            Long bookingId = emailRequest.getBookId();
            Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            switch (emailRequest.getTypeEmail()) {
                case CONFIRMATION:
                    emailService.sendBookingConfirmationEmail(booking);
                    break;
                case CANCELLATION:
                    emailService.sendBookingCancellationEmail(booking);
                    break;
                case SUCCESS_PAYMENT:
                    emailService.sendPaymentSuccessEmail(booking);
                    break;
                default:
                    log.warn("Unknown email type: {}", emailRequest.getTypeEmail());
            }
        } catch (Exception e) {
            log.error("Failed to process email: {}", message, e);
        }
    }

}
