package com.example;

import com.example.hotelbooking.dto.user.UserRequestDto;
import com.example.hotelbooking.dto.user.UserResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Интеграционные тесты для CRUD операций над 'Пользователь'")
public class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // ID созданного пользователя для дальнейших тестов
    private static Long createdUserId;

    @Test
    @Order(1)
    @DisplayName("Регистрация пользователя: проверка создания нового пользователя")
    public void testRegisterUser() {
        // Формируем запрос на регистрацию пользователя
        UserRequestDto request = new UserRequestDto();
        request.setUsername("john_doe");
        request.setPassword("secret");
        request.setEmail("john@example.com");
        request.setRole("USER");

        // Отправляем POST-запрос для регистрации
        ResponseEntity<UserResponseDto> response = restTemplate.postForEntity("/api/users/register", request, UserResponseDto.class);

        // Проверяем, что статус ответа равен 201 (Created)
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Пользователь должен быть создан с HTTP статусом 201");
        UserResponseDto createdUser = response.getBody();
        assertNotNull(createdUser, "Ответ не должен быть пустым");
        assertNotNull(createdUser.getId(), "Созданный пользователь должен иметь ID");
        assertEquals("john_doe", createdUser.getUsername(), "Имя пользователя должно совпадать");
        // Сохраняем ID для дальнейших тестов
        createdUserId = createdUser.getId();
    }

    @Test
    @Order(2)
    @DisplayName("Получение пользователя: проверка получения пользователя по ID")
    public void testGetUser() {
        // Отправляем GET-запрос для получения пользователя по созданному ID
        ResponseEntity<UserResponseDto> response = restTemplate.getForEntity("/api/users/" + createdUserId, UserResponseDto.class);
        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Пользователь должен быть найден (HTTP 200)");
        UserResponseDto user = response.getBody();
        assertNotNull(user, "Ответ не должен быть пустым");
        assertEquals(createdUserId, user.getId(), "ID пользователя должен совпадать с созданным");
    }

    @Test
    @Order(3)
    @DisplayName("Обновление пользователя: проверка обновления данных пользователя")
    public void testUpdateUser() {
        // Формируем запрос для обновления пользователя
        UserRequestDto updateRequest = new UserRequestDto();
        updateRequest.setUsername("john_updated");
        updateRequest.setPassword("new_secret");
        updateRequest.setEmail("john_updated@example.com");
        updateRequest.setRole("USER");

        HttpEntity<UserRequestDto> requestEntity = new HttpEntity<>(updateRequest);
        // Отправляем PUT-запрос для обновления пользователя
        ResponseEntity<UserResponseDto> response = restTemplate.exchange("/api/users/" + createdUserId, HttpMethod.PUT, requestEntity, UserResponseDto.class);
        // Проверяем, что статус ответа равен 200 (OK)
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Обновление пользователя должно вернуть HTTP 200");

        UserResponseDto updatedUser = response.getBody();
        assertNotNull(updatedUser, "Ответ не должен быть пустым");
        assertEquals("john_updated", updatedUser.getUsername(), "Имя пользователя должно быть обновлено");
        assertEquals("john_updated@example.com", updatedUser.getEmail(), "Email должен быть обновлен");
    }

    @Test
    @Order(4)
    @DisplayName("Удаление пользователя: проверка удаления и возврата ошибки при запросе удаленного пользователя")
    public void testDeleteUser() {
        // Отправляем DELETE-запрос для удаления пользователя
        restTemplate.delete("/api/users/" + createdUserId);
        // После удаления отправляем GET-запрос, ожидаем статус 404 (Not Found)
        ResponseEntity<String> response = restTemplate.getForEntity("/api/users/" + createdUserId, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "После удаления пользователь не должен быть найден (HTTP 404)");
    }
}
