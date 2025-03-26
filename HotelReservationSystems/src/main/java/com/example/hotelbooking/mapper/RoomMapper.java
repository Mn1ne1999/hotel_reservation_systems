package com.example.hotelbooking.mapper;

import com.example.hotelbooking.dto.room.RoomRequestDto;
import com.example.hotelbooking.dto.room.RoomResponseDto;
import com.example.hotelbooking.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    // Преобразование из DTO в сущность.
    // Игнорируем поле id и поле hotel (его установим отдельно по hotelId).
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    Room toEntity(RoomRequestDto dto);

    // Преобразование из сущности в DTO.
    // Маппим hotel.id в hotelId.
    @Mapping(target = "hotelId", source = "hotel.id")
    RoomResponseDto toResponseDto(Room room);
}
