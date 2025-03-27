package com.example.hotelbooking.repository;

import com.example.hotelbooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Опционально: метод для получения всех бронирований по ID комнаты
    List<Booking> findByRoomId(Long roomId);
}
