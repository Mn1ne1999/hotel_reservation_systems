package com.example.hotelbooking.service;

import com.example.hotelbooking.entity.Booking;
import com.example.hotelbooking.entity.Room;
import com.example.hotelbooking.entity.User;
import com.example.hotelbooking.repository.BookingRepository;
import com.example.hotelbooking.repository.RoomRepository;
import com.example.hotelbooking.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    /**
     * Создает бронирование, если даты не пересекаются с существующими бронированиями для комнаты.
     */
    public Booking createBooking(Booking booking, Long roomId, Long userId) {
        // Проверяем, что комната существует
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Комната не найдена с id: " + roomId));
        // Проверяем, что пользователь существует
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с id: " + userId));

        booking.setRoom(room);
        booking.setUser(user);

        // Дата заезда должна быть строго раньше даты выезда
        if (!booking.getCheckInDate().isBefore(booking.getCheckOutDate())) {
            throw new IllegalArgumentException("Дата заезда должна быть раньше даты выезда");
        }

        // Проверка пересечения дат
        List<Booking> existingBookings = bookingRepository.findByRoomId(roomId);
        for (Booking existing : existingBookings) {
            if (datesOverlap(booking.getCheckInDate(), booking.getCheckOutDate(),
                    existing.getCheckInDate(), existing.getCheckOutDate())) {
                throw new IllegalArgumentException("Комната уже забронирована на выбранные даты");
            }
        }

        // Сохраняем бронирование
        return bookingRepository.save(booking);
    }

    /**
     * Возвращает список всех бронирований.
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Проверяет, пересекаются ли два периода дат.
     */
    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        // Пересекаются, если start1 < end2 и end1 > start2
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
