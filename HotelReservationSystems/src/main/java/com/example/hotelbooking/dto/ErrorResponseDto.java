package com.example.hotelbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO для представления информации об ошибке.
 */
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDto {
    // Время возникновения ошибки
    private LocalDateTime timestamp;
    // Основное сообщение об ошибке
    private String message;
    // Дополнительные подробности (например, описание причины)
    private String details;
}
