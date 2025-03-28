package com.example;

import com.example.hotelbooking.dto.user.UserRequestDto;
import com.example.hotelbooking.dto.user.UserResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Интеграционные тесты для пользователей (с Basic Auth)")
public class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Используем уникальное имя для избежания конфликтов
    private static final String INITIAL_USERNAME = "john_doe_" + UUID.randomUUID().toString().substring(0, 8);
    private static final String PASSWORD = "secret";
    private static String currentUsername = INITIAL_USERNAME;
    private static Long createdUserId;

    @BeforeAll
    @DisplayName("Очистка базы данных и регистрация администратора")
    public void init() {
        // Очистка всех таблиц (порядок может варьироваться, если используются внешние ключи)
        // Здесь предполагается, что используются таблицы: bookings, rooms, hotels, users
        jdbcTemplate.execute("TRUNCATE TABLE bookings, rooms, hotels, users RESTART IDENTITY CASCADE");

        // Регистрируем администратора
        try {
            UserRequestDto adminRequest = new UserRequestDto();
            adminRequest.setUsername("admin_user_" + UUID.randomUUID().toString().substring(0, 8));
            adminRequest.setPassword("adminPass");
            adminRequest.setEmail("admin@example.com");
            adminRequest.setRole("ADMIN");
            ResponseEntity<UserResponseDto> adminResponse = restTemplate.postForEntity("/api/users/register", adminRequest, UserResponseDto.class);
            // Можно сохранить ID администратора, если потребуется
        } catch (Exception e) {
            System.out.println("Ошибка регистрации администратора: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Регистрация пользователя (без аутентификации) должна вернуть 201")
    public void testRegisterUser() {
        UserRequestDto request = new UserRequestDto();
        request.setUsername(INITIAL_USERNAME);
        request.setPassword(PASSWORD);
        request.setEmail("john@example.com");
        request.setRole("USER");

        ResponseEntity<UserResponseDto> response = restTemplate.postForEntity("/api/users/register", request, UserResponseDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Пользователь должен быть создан с HTTP статусом 201");
        UserResponseDto createdUser = response.getBody();
        assertNotNull(createdUser, "Ответ не должен быть пустым");
        createdUserId = createdUser.getId();
    }

    @Test
    @Order(2)
    @DisplayName("Получение пользователя (с аутентификацией) должно вернуть 200")
    public void testGetUserWithAuth() {
        TestRestTemplate authRestTemplate = restTemplate.withBasicAuth(currentUsername, PASSWORD);
        ResponseEntity<UserResponseDto> response = authRestTemplate.getForEntity("/api/users/" + createdUserId, UserResponseDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Пользователь должен быть найден (HTTP 200)");
        UserResponseDto user = response.getBody();
        assertNotNull(user, "Ответ не должен быть пустым");
        assertEquals(createdUserId, user.getId(), "ID пользователя должен совпадать с созданным");
    }

    @Test
    @Order(3)
    @DisplayName("Обновление пользователя (с аутентификацией) должно вернуть 200")
    public void testUpdateUserWithAuth() {
        UserRequestDto updateRequest = new UserRequestDto();
        updateRequest.setUsername("john_updated_" + UUID.randomUUID().toString().substring(0, 8));
        updateRequest.setPassword(PASSWORD);
        updateRequest.setEmail("john_updated@example.com");
        updateRequest.setRole("USER");

        HttpEntity<UserRequestDto> requestEntity = new HttpEntity<>(updateRequest);
        TestRestTemplate authRestTemplate = restTemplate.withBasicAuth(currentUsername, PASSWORD);
        ResponseEntity<UserResponseDto> response = authRestTemplate.exchange("/api/users/" + createdUserId, HttpMethod.PUT, requestEntity, UserResponseDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Обновление пользователя должно вернуть HTTP 200");
        UserResponseDto updatedUser = response.getBody();
        assertNotNull(updatedUser, "Ответ не должен быть пустым");
        assertEquals(updateRequest.getUsername(), updatedUser.getUsername(), "Имя пользователя должно быть обновлено");
        assertEquals(updateRequest.getEmail(), updatedUser.getEmail(), "Email должен быть обновлен");
        // Обновляем текущее имя для аутентификации
        currentUsername = updateRequest.getUsername();
    }

    @Test
    @Order(4)
    @DisplayName("Удаление пользователя: проверка удаления и возврата ошибки при запросе удаленного пользователя")
    public void testDeleteUserWithAuth() {
        TestRestTemplate userRestTemplate = restTemplate.withBasicAuth(currentUsername, PASSWORD);
        userRestTemplate.delete("/api/users/" + createdUserId);

        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth("admin_user", "adminPass");
        ResponseEntity<String> response = adminRestTemplate.getForEntity("/api/users/" + createdUserId, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "После удаления пользователь не должен быть найден (HTTP 404)");
    }
}
