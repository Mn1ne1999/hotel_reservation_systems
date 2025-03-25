package com.example.hotelbooking.dto.hotel;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelRequestDto {
    private String name;
    private String title;
    private String city;
    private String address;
    private Double distanceFromCenter;
    // rating и ratingCount здесь отсутствуют, т.к. они недоступны для изменения напрямую
}
