package com.example.hotelbooking.mapper;

import com.example.hotelbooking.dto.user.UserRequestDto;
import com.example.hotelbooking.dto.user.UserResponseDto;
import com.example.hotelbooking.entity.Role;
import com.example.hotelbooking.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Преобразование из DTO в сущность.
    // Игнорируем поле id, так как оно генерируется.
    @Mapping(target = "id", ignore = true)
    // Преобразуем строку role в Enum Role. MapStruct может делать это автоматически,
    // если имена совпадают, иначе можно добавить expression.
    User toEntity(UserRequestDto dto);

    // Преобразование из сущности в DTO.
    // Преобразуем Role в строку.
    @Mapping(target = "role", expression = "java(user.getRole().toString())")
    UserResponseDto toResponseDto(User user);
}
