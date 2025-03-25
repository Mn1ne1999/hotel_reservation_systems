package com.example.hotelbooking.exception;

import com.example.hotelbooking.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Глобальный обработчик исключений, который перехватывает ошибки и возвращает структурированный ответ.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка исключения, когда не найдена сущность.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                LocalDateTime.now(),
                ex.getMessage(),
                "Сущность не найдена"
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Обработка исключений, связанных с некорректными аргументами.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                LocalDateTime.now(),
                ex.getMessage(),
                "Некорректный ввод данных"
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка ошибок валидации (@Valid и т.п.).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // Здесь можно более детально собрать ошибки валидации из ex.getBindingResult()
        ErrorResponseDto error = new ErrorResponseDto(
                LocalDateTime.now(),
                "Ошибка валидации",
                ex.getBindingResult().toString()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка всех прочих исключений.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                LocalDateTime.now(),
                ex.getMessage(),
                "Внутренняя ошибка сервера"
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
