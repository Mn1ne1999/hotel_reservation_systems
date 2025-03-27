package com.example.hotelbooking.controller;

import com.example.hotelbooking.dto.booking.BookingRequestDto;
import com.example.hotelbooking.dto.booking.BookingResponseDto;
import com.example.hotelbooking.entity.Booking;
import com.example.hotelbooking.mapper.BookingMapper;
import com.example.hotelbooking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    public BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    // Создание бронирования
    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody BookingRequestDto requestDto) {
        // Преобразуем DTO в сущность
        Booking booking = bookingMapper.toEntity(requestDto);

        // Вызываем сервис для проверки дат и сохранения
        Booking savedBooking = bookingService.createBooking(
                booking,
                requestDto.getRoomId(),
                requestDto.getUserId()
        );

        // Преобразуем сущность обратно в DTO
        BookingResponseDto responseDto = bookingMapper.toResponseDto(savedBooking);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // Получение списка всех бронирований
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        List<BookingResponseDto> responseList = bookings.stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }
}
