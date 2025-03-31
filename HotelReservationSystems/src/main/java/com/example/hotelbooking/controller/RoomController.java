package com.example.hotelbooking.controller;

import com.example.hotelbooking.dto.room.RoomRequestDto;
import com.example.hotelbooking.dto.room.RoomResponseDto;
import com.example.hotelbooking.entity.Room;
import com.example.hotelbooking.mapper.RoomMapper;
import com.example.hotelbooking.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hotelbooking.dto.PagedResponseDto;
import com.example.hotelbooking.specification.RoomSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    public RoomController(RoomService roomService, RoomMapper roomMapper) {
        this.roomService = roomService;
        this.roomMapper = roomMapper;
    }

    // Создание комнаты
    @PostMapping
    public ResponseEntity<RoomResponseDto> createRoom(@RequestBody RoomRequestDto requestDto) {
        Room room = roomMapper.toEntity(requestDto);
        // Создание комнаты с передачей ID отеля из DTO
        Room createdRoom = roomService.createRoom(room, requestDto.getHotelId());
        return ResponseEntity.status(HttpStatus.CREATED).body(roomMapper.toResponseDto(createdRoom));
    }

    // Получение комнаты по ID
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable Long id) {
        Room room = roomService.getRoomById(id);
        return ResponseEntity.ok(roomMapper.toResponseDto(room));
    }

    // Получение списка всех комнат
    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponseDto> responseList = rooms.stream()
                .map(roomMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    // Обновление комнаты
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDto> updateRoom(
            @PathVariable Long id,
            @RequestBody RoomRequestDto requestDto) {
        Room roomData = roomMapper.toEntity(requestDto);
        Room updatedRoom = roomService.updateRoom(id, roomData, requestDto.getHotelId());
        return ResponseEntity.ok(roomMapper.toResponseDto(updatedRoom));
    }

    // Удаление комнаты
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    // Новый эндпоинт для поиска комнат с фильтрацией и пагинацией
    @GetMapping("/search")
    public ResponseEntity<PagedResponseDto<RoomResponseDto>> searchRooms(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer maxGuests,
            @RequestParam(required = false) String checkInDate,
            @RequestParam(required = false) String checkOutDate,
            @RequestParam(required = false) Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Преобразуем checkInDate и checkOutDate, если они заданы
        LocalDate checkIn = (checkInDate != null && !checkInDate.isEmpty()) ? LocalDate.parse(checkInDate) : null;
        LocalDate checkOut = (checkOutDate != null && !checkOutDate.isEmpty()) ? LocalDate.parse(checkOutDate) : null;

        Specification<Room> spec = RoomSpecification.getRoomsByFilter(roomId, name, minPrice, maxPrice, maxGuests, checkIn, checkOut, hotelId);
        Page<Room> roomPage = roomService.getRoomsFiltered(PageRequest.of(page, size), spec);

        List<RoomResponseDto> content = roomPage.getContent().stream()
                .map(roomMapper::toResponseDto)
                .collect(Collectors.toList());

        PagedResponseDto<RoomResponseDto> response = new PagedResponseDto<>();
        response.setContent(content);
        response.setTotalElements(roomPage.getTotalElements());
        response.setTotalPages(roomPage.getTotalPages());
        response.setCurrentPage(page);
        response.setPageSize(size);

        return ResponseEntity.ok(response);
    }
}
