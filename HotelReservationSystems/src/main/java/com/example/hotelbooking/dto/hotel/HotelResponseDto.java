package com.example.hotelbooking.dto.hotel;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponseDto {
    private Long id;
    private String name;
    private String title;
    private String city;
    private String address;
    private Double distanceFromCenter;
    private Double rating;      // текущее значение рейтинга
    private Integer ratingCount; // общее число оценок
}
