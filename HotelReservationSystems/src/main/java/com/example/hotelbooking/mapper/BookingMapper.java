package com.example.hotelbooking.mapper;

import com.example.hotelbooking.dto.booking.BookingRequestDto;
import com.example.hotelbooking.dto.booking.BookingResponseDto;
import com.example.hotelbooking.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    // Из DTO в сущность (room и user задаются в сервисе, поэтому игнорируем)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "user", ignore = true)
    Booking toEntity(BookingRequestDto dto);

    // Из сущности в DTO (room.id -> roomId, user.id -> userId)
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "userId", source = "user.id")
    BookingResponseDto toResponseDto(Booking booking);
}
