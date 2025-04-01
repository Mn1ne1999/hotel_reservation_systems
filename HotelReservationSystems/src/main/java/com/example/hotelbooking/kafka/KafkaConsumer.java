package com.example.hotelbooking.kafka;

import com.example.hotelbooking.entity.Statistic;
import com.example.hotelbooking.service.StatisticService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private final StatisticService statisticService;

    public KafkaConsumer(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @KafkaListener(topics = "topic_name", groupId = "test-group")
    public void listen(String message) {
        System.out.println("Получено сообщение из Kafka: " + message);
        try {
            // Ожидается, что сообщение имеет формат: "eventType,userId,details"
            String[] parts = message.split(",");
            if (parts.length >= 3) {
                String eventType = parts[0].trim();
                Long userId = Long.parseLong(parts[1].trim());
                String details = parts[2].trim();
                Statistic stat = new Statistic();
                stat.setEventType(eventType);
                stat.setUserId(userId);
                stat.setDetails(details);
                statisticService.saveStatistic(stat);
                System.out.println("Статистика сохранена: " + stat);
            }
        } catch (Exception e) {
            System.err.println("Ошибка обработки события: " + e.getMessage());
        }
    }
}
