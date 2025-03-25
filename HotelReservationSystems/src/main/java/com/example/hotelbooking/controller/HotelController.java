package com.example.hotelbooking.controller;

import com.example.hotelbooking.dto.PagedResponseDto;
import com.example.hotelbooking.dto.hotel.HotelRequestDto;
import com.example.hotelbooking.dto.hotel.HotelResponseDto;
import com.example.hotelbooking.entity.Hotel;
import com.example.hotelbooking.mapper.HotelMapper;
import com.example.hotelbooking.service.HotelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final HotelMapper hotelMapper;

    public HotelController(HotelService hotelService, HotelMapper hotelMapper) {
        this.hotelService = hotelService;
        this.hotelMapper = hotelMapper;
    }

    // 1) Создание отеля
    @PostMapping
    public ResponseEntity<HotelResponseDto> createHotel(@RequestBody HotelRequestDto requestDto) {
        // Преобразуем DTO в сущность
        Hotel hotel = hotelMapper.toEntity(requestDto);
        // Изначально рейтинг = 0, ratingCount = 0 (или инициализируем в конструкторе)
        hotel.setRating(0.0);
        hotel.setRatingCount(0);

        // Сохраняем
        Hotel savedHotel = hotelService.createHotel(hotel);

        // Возвращаем DTO ответа
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelMapper.toResponseDto(savedHotel));
    }

    // 2) Поиск отеля по ID
    @GetMapping("/{id}")
    public ResponseEntity<HotelResponseDto> getHotelById(@PathVariable Long id) {
        Hotel hotel = hotelService.getHotelById(id);
        return ResponseEntity.ok(hotelMapper.toResponseDto(hotel));
    }

    // 3) Получение списка всех отелей (без пагинации)
    @GetMapping
    public ResponseEntity<List<HotelResponseDto>> getAllHotels() {
        List<Hotel> hotels = hotelService.getAllHotels();
        List<HotelResponseDto> responseList = hotels.stream()
                .map(hotelMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    // 4) Редактирование отеля
    @PutMapping("/{id}")
    public ResponseEntity<HotelResponseDto> updateHotel(
            @PathVariable Long id,
            @RequestBody HotelRequestDto requestDto
    ) {
        // Преобразуем DTO в сущность (только поля, которые можно менять)
        Hotel hotelData = hotelMapper.toEntity(requestDto);
        Hotel updatedHotel = hotelService.updateHotel(id, hotelData);
        return ResponseEntity.ok(hotelMapper.toResponseDto(updatedHotel));
    }

    // 5) Удаление отеля
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

//    Добавление эндпоинта с пагинацией
//    Вернуть список отелей постранично
    @GetMapping("/paged")
    public ResponseEntity<PagedResponseDto<HotelResponseDto>> getHotelsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Hotel> hotelPage = hotelService.getHotelsPage(PageRequest.of(page, size));

        List<HotelResponseDto> content = hotelPage.getContent().stream()
                .map(hotelMapper::toResponseDto)
                .collect(Collectors.toList());

        PagedResponseDto<HotelResponseDto> response = new PagedResponseDto<>();
        response.setContent(content);
        response.setTotalElements(hotelPage.getTotalElements());
        response.setTotalPages(hotelPage.getTotalPages());
        response.setCurrentPage(page);
        response.setPageSize(size);

        return ResponseEntity.ok(response);
    }

}
