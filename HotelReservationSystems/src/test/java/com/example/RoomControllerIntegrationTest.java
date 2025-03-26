package com.example;

import com.example.hotelbooking.dto.hotel.HotelRequestDto;
import com.example.hotelbooking.dto.hotel.HotelResponseDto;
import com.example.hotelbooking.dto.room.RoomRequestDto;
import com.example.hotelbooking.dto.room.RoomResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Интеграционные тесты для CRUD операций над 'Комната'")
public class RoomControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // ID созданной комнаты для дальнейших тестов
    private Long createdRoomId;

    // ID созданного отеля, необходим для создания комнаты
    private Long hotelId;

    @BeforeAll
    @DisplayName("Создание отеля для тестирования")
    public void createHotelForTests() {
        // Формируем запрос для создания отеля
        HotelRequestDto hotelRequest = new HotelRequestDto();
        hotelRequest.setName("Test Hotel");
        hotelRequest.setTitle("Тестовый отель");
        hotelRequest.setCity("Test City");
        hotelRequest.setAddress("Test Address");
        hotelRequest.setDistanceFromCenter(1.0);

        // Отправляем POST-запрос для создания отеля
        ResponseEntity<HotelResponseDto> hotelResponse = restTemplate.postForEntity("/api/hotels", hotelRequest, HotelResponseDto.class);
        // Проверяем, что статус ответа равен 201 (Created)
        assertEquals(HttpStatus.CREATED, hotelResponse.getStatusCode(), "Отель должен быть создан с HTTP статусом 201");
        HotelResponseDto createdHotel = hotelResponse.getBody();
        assertNotNull(createdHotel, "Ответ от создания отеля не должен быть пустым");
        // Сохраняем ID отеля для использования при создании комнаты
        hotelId = createdHotel.getId();
    }

    @Test
    @Order(1)
    @DisplayName("Создание комнаты: проверка создания комнаты и корректного ответа")
    public void testCreateRoom() {
        // Формируем запрос для создания комнаты
        RoomRequestDto request = new RoomRequestDto();
        request.setName("Deluxe Room");
        request.setDescription("Просторный номер с видом на море");
        request.setRoomNumber("101");
        request.setPrice(150.0);
        request.setMaxGuests(3);
        request.setUnavailableDates(Set.of(LocalDate.of(2025, 4, 10), LocalDate.of(2025, 4, 11)));
        // Используем ID отеля, созданного ранее
        request.setHotelId(hotelId);

        // Отправляем POST-запрос на создание комнаты
        ResponseEntity<RoomResponseDto> response = restTemplate.postForEntity("/api/rooms", request, RoomResponseDto.class);

        // Проверяем, что статус ответа равен 201 (Created)
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Комната должна быть создана с HTTP статусом 201");
        RoomResponseDto createdRoom = response.getBody();
        assertNotNull(createdRoom, "Ответ не должен быть пустым");
        assertNotNull(createdRoom.getId(), "Созданная комната должна иметь ID");
        assertEquals("Deluxe Room", createdRoom.getName(), "Имя комнаты должно совпадать");
        // Сохраняем ID созданной комнаты для дальнейших тестов
        createdRoomId = createdRoom.getId();
    }

    @Test
    @Order(2)
    @DisplayName("Получение комнаты: проверка получения комнаты по ID")
    public void testGetRoom() {
        // Отправляем GET-запрос для получения комнаты по созданному ID
        ResponseEntity<RoomResponseDto> response = restTemplate.getForEntity("/api/rooms/" + createdRoomId, RoomResponseDto.class);
        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Комната должна быть найдена (HTTP 200)");
        RoomResponseDto room = response.getBody();
        assertNotNull(room, "Ответ не должен быть пустым");
        assertEquals(createdRoomId, room.getId(), "ID комнаты должен совпадать с созданным");
    }

    @Test
    @Order(3)
    @DisplayName("Обновление комнаты: проверка обновления данных комнаты")
    public void testUpdateRoom() {
        // Формируем запрос для обновления комнаты
        RoomRequestDto updateRequest = new RoomRequestDto();
        updateRequest.setName("Обновленный номер");
        updateRequest.setDescription("Обновленное описание");
        updateRequest.setRoomNumber("102");
        updateRequest.setPrice(200.0);
        updateRequest.setMaxGuests(4);
        updateRequest.setUnavailableDates(Set.of(LocalDate.of(2025, 5, 1)));
        updateRequest.setHotelId(hotelId);

        HttpEntity<RoomRequestDto> requestEntity = new HttpEntity<>(updateRequest);
        // Отправляем PUT-запрос для обновления комнаты
        ResponseEntity<RoomResponseDto> response = restTemplate.exchange("/api/rooms/" + createdRoomId, HttpMethod.PUT, requestEntity, RoomResponseDto.class);
        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Обновление комнаты должно вернуть HTTP 200");

        RoomResponseDto updatedRoom = response.getBody();
        assertNotNull(updatedRoom, "Ответ не должен быть пустым");
        assertEquals("Обновленный номер", updatedRoom.getName(), "Имя комнаты должно быть обновлено");
        assertEquals("102", updatedRoom.getRoomNumber(), "Номер комнаты должен быть обновлен");
        assertEquals(200.0, updatedRoom.getPrice(), "Цена комнаты должна быть обновлена");
    }

    @Test
    @Order(4)
    @DisplayName("Удаление комнаты: проверка удаления и возврата ошибки при запросе удаленной комнаты")
    public void testDeleteRoom() {
        // Отправляем запрос на удаление комнаты
        restTemplate.delete("/api/rooms/" + createdRoomId);
        // После удаления попытка получить комнату должна вернуть 404 (Not Found)
        ResponseEntity<String> response = restTemplate.getForEntity("/api/rooms/" + createdRoomId, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "После удаления комната не должна быть найдена (HTTP 404)");
    }
}
