package com.example.hotelbooking.dto.hotel;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingUpdateRequestDto {
    // Новая оценка (от 1 до 5)
    private Double newMark;
}
