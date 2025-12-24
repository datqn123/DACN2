package com.example.dacn2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.dacn2.dto.request.email.EmailRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaProducerServicce {

    private static final String TOPIC = "MY_TOPIC";
    private static final String EMAIL_TOPIC = "email_topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessage(String message) {
        System.out.println(String.format("#### -> Producing message -> %s", message));
        this.kafkaTemplate.send(TOPIC, message);
    }

    public void sendEmailNotification(EmailRequest emailRequest) {
        try {
            String message = objectMapper.writeValueAsString(emailRequest);
            this.kafkaTemplate.send(EMAIL_TOPIC, message);
            System.out.println(String.format("#### -> Producing message -> %s", message));
        } catch (Exception e) {
        }
    }

}
