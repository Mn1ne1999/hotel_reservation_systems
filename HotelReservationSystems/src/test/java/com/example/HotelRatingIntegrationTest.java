package com.example;

import com.example.hotelbooking.dto.hotel.HotelRequestDto;
import com.example.hotelbooking.dto.hotel.HotelResponseDto;
import com.example.hotelbooking.dto.hotel.RatingUpdateRequestDto;
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

@SpringBootTest(properties = "spring.profiles.active=test", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Интеграционные тесты для обновления рейтинга отеля")
public class HotelRatingIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // Фиксированные учетные данные администратора
    private static final String ADMIN_USERNAME = "admin_user";
    private static final String ADMIN_PASSWORD = "adminPass";

    private Long hotelId;

    @BeforeAll
    @DisplayName("Регистрация администратора и создание отеля для тестов")
    void setup() {
        // Регистрируем администратора через публичный эндпоинт
        UserRequestDto adminRequest = new UserRequestDto();
        adminRequest.setUsername(ADMIN_USERNAME);
        adminRequest.setPassword(ADMIN_PASSWORD);
        adminRequest.setEmail("admin@example.com");
        adminRequest.setRole("ADMIN");

        ResponseEntity<UserResponseDto> adminResponse = restTemplate.postForEntity("/api/users/register", adminRequest, UserResponseDto.class);
        if (adminResponse.getStatusCode() != HttpStatus.CREATED) {
            // Если администратор уже существует, выводим сообщение
            System.out.println("Администратор уже существует или не удалось зарегистрировать администратора.");
        }
        // Используем учетные данные администратора для создания отеля
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);

        HotelRequestDto hotelRequest = new HotelRequestDto();
        // Добавляем уникальность, чтобы избежать конфликтов при повторном запуске
        hotelRequest.setName("Test Hotel " + UUID.randomUUID().toString().substring(0, 8));
        hotelRequest.setTitle("Hotel Title");
        hotelRequest.setCity("Test City");
        hotelRequest.setAddress("Test Address");
        hotelRequest.setDistanceFromCenter(1.0);

        ResponseEntity<HotelResponseDto> hotelResponse = adminRestTemplate.postForEntity("/api/hotels", hotelRequest, HotelResponseDto.class);
        assertEquals(HttpStatus.CREATED, hotelResponse.getStatusCode(), "Отель должен быть создан (HTTP 201)");
        hotelId = hotelResponse.getBody().getId();
    }

    @Test
    @Order(1)
    @DisplayName("Обновление рейтинга отеля: первая оценка")
    void testUpdateHotelRating_FirstScore() {
        // Первоначально рейтинг = 0, numberOfRatings = 0
        RatingUpdateRequestDto requestDto = new RatingUpdateRequestDto(4.0);
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);
        ResponseEntity<HotelResponseDto> response = adminRestTemplate.postForEntity("/api/hotels/" + hotelId + "/rating", requestDto, HotelResponseDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Обновление рейтинга должно вернуть HTTP 200");
        HotelResponseDto updatedHotel = response.getBody();
        assertNotNull(updatedHotel, "Ответ не должен быть пустым");
        // После первой оценки рейтинг должен стать равным оценке
        assertEquals(4.0, updatedHotel.getRating(), "Новый рейтинг должен быть равен 4.0");
    }

    @Test
    @Order(2)
    @DisplayName("Обновление рейтинга отеля: вторая оценка")
    void testUpdateHotelRating_SecondScore() {
        // После первой оценки: rating = 4.0, numberOfRatings = 1
        // Добавляем вторую оценку, например 5.0, ожидаемый новый рейтинг = (4.0*1+5.0)/2 = 4.5
        RatingUpdateRequestDto requestDto = new RatingUpdateRequestDto(5.0);
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);
        ResponseEntity<HotelResponseDto> response = adminRestTemplate.postForEntity("/api/hotels/" + hotelId + "/rating", requestDto, HotelResponseDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Обновление рейтинга должно вернуть HTTP 200");
        HotelResponseDto updatedHotel = response.getBody();
        assertNotNull(updatedHotel, "Ответ не должен быть пустым");
        assertEquals(4.5, updatedHotel.getRating(), "Новый рейтинг должен быть равен 4.5");
    }
}
