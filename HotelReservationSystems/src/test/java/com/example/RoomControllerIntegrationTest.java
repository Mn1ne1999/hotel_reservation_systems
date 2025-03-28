package com.example;

import com.example.hotelbooking.dto.hotel.HotelRequestDto;
import com.example.hotelbooking.dto.hotel.HotelResponseDto;
import com.example.hotelbooking.dto.room.RoomRequestDto;
import com.example.hotelbooking.dto.room.RoomResponseDto;
import com.example.hotelbooking.dto.user.UserRequestDto;
import com.example.hotelbooking.dto.user.UserResponseDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Интеграционные тесты для операций над 'Комната' (админ)")
public class RoomControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String ADMIN_USERNAME = "admin_user";
    private static final String ADMIN_PASSWORD = "adminPass";

    private Long createdHotelId;
    private Long createdRoomId;
    private Long adminUserId;

    @BeforeAll
    @DisplayName("Регистрация администратора и создание отеля, комнаты для тестов")
    void setup() {
        // Регистрируем администратора, если его нет
        try {
            UserRequestDto adminRequest = new UserRequestDto();
            adminRequest.setUsername(ADMIN_USERNAME);
            adminRequest.setPassword(ADMIN_PASSWORD);
            adminRequest.setEmail("admin@example.com");
            adminRequest.setRole("ADMIN");
            ResponseEntity<UserResponseDto> adminResponse = restTemplate.postForEntity("/api/users/register", adminRequest, UserResponseDto.class);
            if (adminResponse.getStatusCode() == HttpStatus.CREATED) {
                adminUserId = adminResponse.getBody().getId();
            }
        } catch(Exception e) {
            System.out.println("Администратор уже существует: " + e.getMessage());
        }
        // Если adminUserId не получен, пытаемся получить его через GET с Basic Auth
        if (adminUserId == null) {
            TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);
            ResponseEntity<UserResponseDto[]> response = adminRestTemplate.getForEntity("/api/users", UserResponseDto[].class);
            if (response.getBody() != null) {
                for (UserResponseDto user : response.getBody()) {
                    if (ADMIN_USERNAME.equals(user.getUsername())) {
                        adminUserId = user.getId();
                        break;
                    }
                }
            }
        }
        assertNotNull(adminUserId, "ID администратора не должен быть null");

        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);

        // Создаем отель
        HotelRequestDto hotelRequest = new HotelRequestDto();
        // Добавляем уникальный суффикс для отеля, чтобы избежать конфликта
        hotelRequest.setName("Test Hotel " + UUID.randomUUID().toString().substring(0, 8));
        hotelRequest.setTitle("Hotel Title");
        hotelRequest.setCity("Test City");
        hotelRequest.setAddress("Test Address");
        hotelRequest.setDistanceFromCenter(1.0);
        ResponseEntity<HotelResponseDto> hotelResponse = adminRestTemplate.postForEntity("/api/hotels", hotelRequest, HotelResponseDto.class);
        assertEquals(HttpStatus.CREATED, hotelResponse.getStatusCode(), "Отель должен быть создан (HTTP 201)");
        createdHotelId = hotelResponse.getBody().getId();

        // Создаем комнату
        RoomRequestDto roomRequest = new RoomRequestDto();
        roomRequest.setName("Test Room");
        roomRequest.setDescription("Room Description");
        roomRequest.setRoomNumber("101");
        roomRequest.setPrice(120.0);
        roomRequest.setMaxGuests(2);
        roomRequest.setUnavailableDates(Collections.emptySet());
        roomRequest.setHotelId(createdHotelId);
        ResponseEntity<RoomResponseDto> roomResponse = adminRestTemplate.postForEntity("/api/rooms", roomRequest, RoomResponseDto.class);
        assertEquals(HttpStatus.CREATED, roomResponse.getStatusCode(), "Комната должна быть создана (HTTP 201)");
        createdRoomId = roomResponse.getBody().getId();
    }

    @Test
    @Order(1)
    @DisplayName("Проверка создания комнаты (POST /api/rooms)")
    void testCreateRoom() {
        assertNotNull(createdRoomId, "Комната должна быть создана в setup");
    }

    @Test
    @Order(2)
    @DisplayName("Проверка получения комнаты (GET /api/rooms/{id})")
    void testGetRoom() {
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);
        ResponseEntity<RoomResponseDto> response = adminRestTemplate.getForEntity("/api/rooms/" + createdRoomId, RoomResponseDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Комната должна быть найдена (HTTP 200)");
        RoomResponseDto room = response.getBody();
        assertNotNull(room, "Ответ не должен быть пустым");
        assertEquals(createdRoomId, room.getId(), "ID комнаты должен совпадать");
    }

    @Test
    @Order(3)
    @DisplayName("Проверка обновления комнаты (PUT /api/rooms/{id})")
    void testUpdateRoom() {
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);
        RoomRequestDto updateRequest = new RoomRequestDto();
        updateRequest.setName("Updated Room");
        updateRequest.setDescription("Updated Description");
        updateRequest.setRoomNumber("102");
        updateRequest.setPrice(150.0);
        updateRequest.setMaxGuests(3);
        updateRequest.setUnavailableDates(Set.of());
        updateRequest.setHotelId(createdHotelId);
        HttpEntity<RoomRequestDto> requestEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<RoomResponseDto> response = adminRestTemplate.exchange("/api/rooms/" + createdRoomId, HttpMethod.PUT, requestEntity, RoomResponseDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Обновление комнаты должно вернуть HTTP 200");
        RoomResponseDto updatedRoom = response.getBody();
        assertNotNull(updatedRoom, "Ответ не должен быть пустым");
        assertEquals("Updated Room", updatedRoom.getName(), "Имя комнаты должно быть обновлено");
        assertEquals("102", updatedRoom.getRoomNumber(), "Номер комнаты должен быть обновлен");
    }

    @Test
    @Order(4)
    @DisplayName("Проверка удаления комнаты (DELETE /api/rooms/{id}) и получение 404 после удаления")
    void testDeleteRoom() {
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);
        adminRestTemplate.delete("/api/rooms/" + createdRoomId);
        ResponseEntity<String> response = adminRestTemplate.getForEntity("/api/rooms/" + createdRoomId, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "После удаления комната не должна быть найдена (HTTP 404)");
    }
}
