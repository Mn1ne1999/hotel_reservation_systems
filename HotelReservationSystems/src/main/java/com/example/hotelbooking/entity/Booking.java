package com.example.hotelbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Дата заезда
    private LocalDate checkInDate;

    // Дата выезда
    private LocalDate checkOutDate;

    // Связь с комнатой (Room)
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // Связь с пользователем (User)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
