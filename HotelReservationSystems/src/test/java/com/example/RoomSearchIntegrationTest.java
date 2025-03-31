package com.example;

import com.example.hotelbooking.dto.hotel.HotelRequestDto;
import com.example.hotelbooking.dto.hotel.HotelResponseDto;
import com.example.hotelbooking.dto.room.RoomRequestDto;
import com.example.hotelbooking.dto.room.RoomResponseDto;
import com.example.hotelbooking.dto.PagedResponseDto;
import com.example.hotelbooking.dto.user.UserRequestDto;
import com.example.hotelbooking.dto.user.UserResponseDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Интеграционные тесты для поиска комнат с фильтрацией")
public class RoomSearchIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // Фиксированные учетные данные администратора
    private static final String ADMIN_USERNAME = "admin_user";
    private static final String ADMIN_PASSWORD = "adminPass";

    private Long hotelId;
    private Long roomId;

    @BeforeAll
    @DisplayName("Регистрация администратора и создание отеля, комнаты для тестов поиска")
    void setup() {
        // Регистрируем администратора через публичный эндпоинт, если его еще нет
        try {
            UserRequestDto adminRequest = new UserRequestDto();
            adminRequest.setUsername(ADMIN_USERNAME);
            adminRequest.setPassword(ADMIN_PASSWORD);
            adminRequest.setEmail("admin@example.com");
            adminRequest.setRole("ADMIN");
            restTemplate.postForEntity("/api/users/register", adminRequest, UserResponseDto.class);
        } catch (Exception e) {
            System.out.println("Администратор уже существует или ошибка регистрации: " + e.getMessage());
        }
        // Используем учетные данные администратора для создания отеля и комнаты
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);

        // Создаем отель
        HotelRequestDto hotelRequest = new HotelRequestDto();
        hotelRequest.setName("Test Hotel " + UUID.randomUUID().toString().substring(0, 8));
        hotelRequest.setTitle("Отель для поиска");
        hotelRequest.setCity("Москва");
        hotelRequest.setAddress("ул. Пушкина, 1");
        hotelRequest.setDistanceFromCenter(3.0);
        ResponseEntity<HotelResponseDto> hotelResponse = adminRestTemplate.postForEntity(
                "/api/hotels", hotelRequest, HotelResponseDto.class);
        assertEquals(HttpStatus.CREATED, hotelResponse.getStatusCode(), "Отель должен быть создан (HTTP 201)");
        hotelId = hotelResponse.getBody().getId();

        // Создаем комнату, связанную с созданным отелем
        RoomRequestDto roomRequest = new RoomRequestDto();
        roomRequest.setName("Search Test Room " + UUID.randomUUID().toString().substring(0,8));
        roomRequest.setDescription("Комната для тестирования поиска");
        roomRequest.setRoomNumber("101");
        roomRequest.setPrice(200.0);
        roomRequest.setMaxGuests(3);
        roomRequest.setHotelId(hotelId);
        ResponseEntity<RoomResponseDto> roomResponse = adminRestTemplate.postForEntity(
                "/api/rooms", roomRequest, RoomResponseDto.class);
        assertEquals(HttpStatus.CREATED, roomResponse.getStatusCode(), "Комната должна быть создана (HTTP 201)");
        roomId = roomResponse.getBody().getId();
    }

    @Test
    @DisplayName("Постраничный поиск комнат с фильтрацией по цене")
    void testSearchRoomsByPrice() {
        // Ищем комнаты с ценой в диапазоне 150 - 250
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);
        String url = "/api/rooms/search?minPrice=150&maxPrice=250&page=0&size=10";
        ResponseEntity<PagedResponseDto> response = adminRestTemplate.getForEntity(url, PagedResponseDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Запрос поиска должен вернуть HTTP 200");
        PagedResponseDto pagedResponse = response.getBody();
        assertNotNull(pagedResponse, "Ответ не должен быть пустым");
        assertTrue(pagedResponse.getTotalElements() > 0, "Должна быть найдена хотя бы одна комната");
    }
}
