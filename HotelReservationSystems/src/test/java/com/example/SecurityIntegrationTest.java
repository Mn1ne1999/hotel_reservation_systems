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
@DisplayName("Интеграционные тесты безопасности (Spring Security)")
public class SecurityIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // Учетные данные зарегистрированного пользователя (например, обычного)
    private static final String USERNAME = "john_doe";
    private static final String PASSWORD = "secret";

    // ID созданного пользователя
    private static Long createdUserId;

    @Test
    @Order(1)
    @DisplayName("Регистрация пользователя доступна без аутентификации (POST /api/users/register)")
    public void testRegisterUserWithoutAuth() {
        UserRequestDto request = new UserRequestDto();
        request.setUsername(USERNAME);
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
    @DisplayName("Доступ к защищенному эндпоинту без аутентификации должен вернуть 401 (GET /api/users/{id})")
    public void testAccessProtectedWithoutAuth() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/users/" + createdUserId, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "Без аутентификации должен возвращаться HTTP 401");
    }

    @Test
    @Order(3)
    @DisplayName("Доступ к защищенному эндпоинту с корректной аутентификацией должен вернуть 200 (GET /api/users/{id})")
    public void testAccessProtectedWithAuth() {
        TestRestTemplate authRestTemplate = restTemplate.withBasicAuth(USERNAME, PASSWORD);
        ResponseEntity<UserResponseDto> response = authRestTemplate.getForEntity("/api/users/" + createdUserId, UserResponseDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "С корректной аутентификацией должен возвращаться HTTP 200");
        UserResponseDto user = response.getBody();
        assertNotNull(user, "Ответ не должен быть пустым");
        assertEquals(createdUserId, user.getId(), "ID пользователя должен совпадать с созданным");
    }

    @Test
    @Order(4)
    @DisplayName("Доступ к защищенному эндпоинту с некорректной аутентификацией должен вернуть 401 или 403")
    public void testAccessProtectedWithWrongAuth() {
        TestRestTemplate wrongAuthRestTemplate = restTemplate.withBasicAuth("wrong_user", "wrong_pass");
        ResponseEntity<String> response = wrongAuthRestTemplate.getForEntity("/api/users/" + createdUserId, String.class);
        assertTrue(response.getStatusCode() == HttpStatus.UNAUTHORIZED || response.getStatusCode() == HttpStatus.FORBIDDEN,
                "При неправильной аутентификации должен возвращаться HTTP 401 или 403");
    }
}
