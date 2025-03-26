package com.example.hotelbooking.dto.room;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDto {
    private Long id;
    private String name;
    private String description;
    private String roomNumber;
    private Double price;
    private Integer maxGuests;
    private Set<LocalDate> unavailableDates;
    // Для удобства возвращаем ID отеля
    private Long hotelId;
}
