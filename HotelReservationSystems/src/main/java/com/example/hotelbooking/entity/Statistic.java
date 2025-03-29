package com.example.hotelbooking.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Statistic {
    @Id
    private String id;

    // Например, ID пользователя, участвующего в событии
    private Long userId;

    // Тип события: регистрация, бронирование и т.д.
    private String eventType;

    // Дополнительные данные в формате JSON (например, даты бронирования)
    private String details;
}
