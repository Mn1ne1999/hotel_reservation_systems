package com.example.hotelbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Название комнаты
    private String name;

    // Описание комнаты
    private String description;

    // Номер комнаты
    private String roomNumber;

    // Цена за номер
    private Double price;

    // Максимальное количество гостей
    private Integer maxGuests;

    // Даты, когда комната недоступна (используем коллекцию LocalDate)
    @ElementCollection
    @CollectionTable(name = "room_unavailable_dates", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "unavailable_date")
    private Set<LocalDate> unavailableDates;

    // Каждая комната принадлежит одному отелю (обязательное поле)
    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;
}
