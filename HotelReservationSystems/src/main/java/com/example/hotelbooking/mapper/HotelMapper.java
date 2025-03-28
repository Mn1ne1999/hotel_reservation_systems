package com.example.hotelbooking.mapper;

import com.example.hotelbooking.dto.hotel.HotelRequestDto;
import com.example.hotelbooking.dto.hotel.HotelResponseDto;
import com.example.hotelbooking.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    // Из DTO (создание/редактирование) в сущность: игнорируем поля id, rating и numberOfRatings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "numberOfRatings", ignore = true)
    Hotel toEntity(HotelRequestDto requestDto);

    // Из сущности в DTO (для редактирования) — если нужно
    HotelRequestDto toRequestDto(Hotel hotel);

    // Из сущности в DTO (полный ответ)
    @Mapping(target = "ratingCount", source = "numberOfRatings")
    HotelResponseDto toResponseDto(Hotel hotel);
}
