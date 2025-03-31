package com.example;

import com.example.hotelbooking.dto.hotel.HotelRequestDto;
import com.example.hotelbooking.dto.hotel.HotelResponseDto;
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
@DisplayName("Интеграционные тесты для поиска отелей с фильтрацией")
public class HotelSearchIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // Фиксированные учетные данные администратора
    private static final String ADMIN_USERNAME = "admin_user";
    private static final String ADMIN_PASSWORD = "adminPass";

    private Long hotelId;

    @BeforeAll
    @DisplayName("Регистрация администратора и создание отеля для тестов поиска")
    void setup() {
        // Регистрируем администратора, если его нет, используя публичный эндпоинт регистрации.
        try {
            UserRequestDto adminRequest = new UserRequestDto();
            adminRequest.setUsername(ADMIN_USERNAME);
            adminRequest.setPassword(ADMIN_PASSWORD);
            adminRequest.setEmail("admin@example.com");
            adminRequest.setRole("ADMIN");

            ResponseEntity<UserResponseDto> adminResponse = restTemplate.postForEntity(
                    "/api/users/register", adminRequest, UserResponseDto.class);
            if (adminResponse.getStatusCode() == HttpStatus.CREATED) {
                System.out.println("Администратор успешно зарегистрирован.");
            }
        } catch (Exception e) {
            System.out.println("Администратор уже существует или ошибка регистрации: " + e.getMessage());
        }

        // Используем учетные данные администратора для создания отеля
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);

        HotelRequestDto hotelRequest = new HotelRequestDto();
        // Уникальное имя отеля, чтобы избежать конфликтов
        hotelRequest.setName("Search Test Hotel " + UUID.randomUUID().toString().substring(0, 8));
        hotelRequest.setTitle("Уютный отель");
        hotelRequest.setCity("Москва");
        hotelRequest.setAddress("ул. Ленина, 10");
        hotelRequest.setDistanceFromCenter(2.5);

        ResponseEntity<HotelResponseDto> response = adminRestTemplate.postForEntity(
                "/api/hotels", hotelRequest, HotelResponseDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Отель должен быть создан (HTTP 201)");
        hotelId = response.getBody().getId();
    }

    @Test
    @DisplayName("Постраничный поиск отелей с фильтрацией по городу")
    void testSearchHotelsByCity() {
        String searchCity = "москва"; // тестируем поиск по городу (регистр не важен)

        // Используем учетные данные администратора для запроса защищенного эндпоинта
        TestRestTemplate authRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);
        ResponseEntity<PagedResponseDto> response = authRestTemplate.getForEntity(
                "/api/hotels/search?city=" + searchCity + "&page=0&size=10",
                PagedResponseDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Запрос поиска должен вернуть HTTP 200");

        PagedResponseDto pagedResponse = response.getBody();
        assertNotNull(pagedResponse, "Ответ не должен быть пустым");
        assertTrue(pagedResponse.getTotalElements() > 0, "Должен быть найден хотя бы один отель");
    }

}
