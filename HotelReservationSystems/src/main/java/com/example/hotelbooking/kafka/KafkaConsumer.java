package com.example.hotelbooking.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "topic_name", groupId = "test-group")
    public void listen(String message) {
        System.out.println("Получено сообщение из Kafka: " + message);
        // Дополнительная логика обработки события, если требуется
    }
}
