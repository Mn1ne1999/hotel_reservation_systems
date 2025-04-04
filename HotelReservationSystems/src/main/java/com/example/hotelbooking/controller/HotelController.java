package com.example.hotelbooking.controller;

import com.example.hotelbooking.dto.PagedResponseDto;
import com.example.hotelbooking.dto.hotel.HotelRequestDto;
import com.example.hotelbooking.dto.hotel.HotelResponseDto;
import com.example.hotelbooking.dto.hotel.RatingUpdateRequestDto;
import com.example.hotelbooking.entity.Hotel;
import com.example.hotelbooking.mapper.HotelMapper;
import com.example.hotelbooking.service.HotelService;
import com.example.hotelbooking.specification.HotelSpecification;
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
        Hotel hotel = hotelMapper.toEntity(requestDto);
        hotel.setRating(0.0);
        hotel.setNumberOfRatings(0);

        Hotel savedHotel = hotelService.createHotel(hotel);

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
        Hotel hotelData = hotelMapper.toEntity(requestDto);
        Hotel updatedHotel = hotelService.updateHotel(id, hotelData);
        return ResponseEntity.ok(hotelMapper.toResponseDto(updatedHotel));
    }

    // 5) Обновление рейтинга отеля
    @PostMapping("/{id}/rating")
    public ResponseEntity<HotelResponseDto> updateRating(@PathVariable Long id, @RequestBody RatingUpdateRequestDto requestDto) {
        if (requestDto.getNewMark() < 1 || requestDto.getNewMark() > 5) {
            return ResponseEntity.badRequest().build();
        }
        Hotel updatedHotel = hotelService.updateHotelRating(id, requestDto.getNewMark());
        return ResponseEntity.ok(hotelMapper.toResponseDto(updatedHotel));
    }

    // 6) Удаление отеля
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    // 7) Пагинированный запрос
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

    @GetMapping("/search")
    public ResponseEntity<PagedResponseDto<HotelResponseDto>> searchHotels(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Double distance,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) Integer numberOfRatings,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Построение спецификации
        org.springframework.data.jpa.domain.Specification<Hotel> spec =
                org.springframework.data.jpa.domain.Specification.where(HotelSpecification.hasId(id))
                        .and(HotelSpecification.hasName(name))
                        .and(HotelSpecification.hasTitle(title))
                        .and(HotelSpecification.hasCity(city))
                        .and(HotelSpecification.hasAddress(address))
                        .and(HotelSpecification.hasDistance(distance))
                        .and(HotelSpecification.hasRating(rating))
                        .and(HotelSpecification.hasNumberOfRatings(numberOfRatings));

        // Получаем постраничный результат
        Page<Hotel> hotelPage = hotelService.getHotelsBySpec(spec, PageRequest.of(page, size));

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
