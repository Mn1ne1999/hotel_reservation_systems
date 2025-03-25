package com.example.hotelbooking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Название отеля
    private String name;

    // Заголовок объявления
    private String title;

    // Город, в котором расположен отель
    private String city;

    // Адрес отеля
    private String address;

    // Расстояние от центра города
    private Double distanceFromCenter;

    // Рейтинг (от 1 до 5). Для удобства используем Double, чтобы хранить среднее значение.
    private Double rating;

    // Количество оценок, на основе которых рассчитан рейтинг
    private Integer ratingCount;
}
