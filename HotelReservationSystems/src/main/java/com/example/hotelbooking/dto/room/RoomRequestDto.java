package com.example.hotelbooking.dto.room;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDto {
    private String name;
    private String description;
    private String roomNumber;
    private Double price;
    private Integer maxGuests;
    // Даты, когда комната недоступна
    private Set<LocalDate> unavailableDates;
    // ID отеля, к которому принадлежит комната
    private Long hotelId;
}
