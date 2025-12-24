package com.example.dacn2.controller.others;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dacn2.service.KafkaProducerServicce;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    @Autowired
    private KafkaProducerServicce kafkaProducerServicce;

    @PostMapping("send")
    public void sendMessage(@RequestParam String message) {
        kafkaProducerServicce.sendMessage(message);
    }

}
