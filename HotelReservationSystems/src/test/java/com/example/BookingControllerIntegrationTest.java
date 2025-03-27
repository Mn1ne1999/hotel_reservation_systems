package com.example.testbooking;

import com.example.hotelbooking.dto.booking.BookingRequestDto;
import com.example.hotelbooking.dto.booking.BookingResponseDto;
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
import org.springframework.http.*;

import org.springframework.boot.test.web.client.TestRestTemplate;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Интеграционные тесты для бронирований (Booking)")
public class BookingControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // Учетные данные (предполагается, что тест проверяет доступ при необходимости)
    // Если безопасность не включена, можно использовать обычный restTemplate
    private static final String ADMIN_USERNAME = "admin_user";
    private static final String ADMIN_PASSWORD = "adminPass";

    // ID созданного пользователя-админа, отеля, комнаты и бронирования
    private Long adminUserId;
    private Long hotelId;
    private Long roomId;
    private Long bookingId;

    @BeforeAll
    @DisplayName("Создание администратора, отеля и комнаты для бронирования")
    void setup() {
        // 1) Регистрируем администратора (если не создан)
        try {
            UserRequestDto adminRequest = new UserRequestDto();
            adminRequest.setUsername(ADMIN_USERNAME);
            adminRequest.setPassword(ADMIN_PASSWORD);
            adminRequest.setEmail("admin@example.com");
            adminRequest.setRole("ADMIN");

            ResponseEntity<UserResponseDto> adminResponse =
                    restTemplate.postForEntity("/api/users/register", adminRequest, UserResponseDto.class);
            adminUserId = adminResponse.getBody().getId();
        } catch(Exception e) {
            System.out.println("Администратор уже существует, код ответа: " + e.getMessage());
        }
        // Если adminUserId остался null, попробуем получить его через GET-запрос
        if (adminUserId == null) {
            TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);
            ResponseEntity<UserResponseDto[]> response =
                    adminRestTemplate.getForEntity("/api/users", UserResponseDto[].class);
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

        // 2) Создаем отель
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);
        HotelRequestDto hotelRequest = new HotelRequestDto();
        hotelRequest.setName("Booking Test Hotel");
        hotelRequest.setTitle("Hotel Title");
        hotelRequest.setCity("Test City");
        hotelRequest.setAddress("Test Address");
        hotelRequest.setDistanceFromCenter(1.0);

        ResponseEntity<HotelResponseDto> hotelResponse =
                adminRestTemplate.postForEntity("/api/hotels", hotelRequest, HotelResponseDto.class);
        assertEquals(HttpStatus.CREATED, hotelResponse.getStatusCode(), "Отель должен быть создан");
        hotelId = hotelResponse.getBody().getId();

        // 3) Создаем комнату
        RoomRequestDto roomRequest = new RoomRequestDto();
        roomRequest.setName("Booking Test Room");
        roomRequest.setDescription("Room Description");
        roomRequest.setRoomNumber("101");
        roomRequest.setPrice(100.0);
        roomRequest.setMaxGuests(2);
        roomRequest.setUnavailableDates(Collections.emptySet());
        roomRequest.setHotelId(hotelId);

        ResponseEntity<RoomResponseDto> roomResponse =
                adminRestTemplate.postForEntity("/api/rooms", roomRequest, RoomResponseDto.class);
        assertEquals(HttpStatus.CREATED, roomResponse.getStatusCode(), "Комната должна быть создана");
        roomId = roomResponse.getBody().getId();
    }


    @Test
    @Order(1)
    @DisplayName("Создание бронирования: проверка успешного создания (POST /api/bookings)")
    void testCreateBooking() {
        // Отправляем POST-запрос с датами, которые не пересекаются
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);

        BookingRequestDto bookingRequest = new BookingRequestDto();
        bookingRequest.setCheckInDate(LocalDate.of(2025, 6, 1));
        bookingRequest.setCheckOutDate(LocalDate.of(2025, 6, 5));
        bookingRequest.setRoomId(roomId);
        bookingRequest.setUserId(adminUserId);

        ResponseEntity<BookingResponseDto> response =
                adminRestTemplate.postForEntity("/api/bookings", bookingRequest, BookingResponseDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Бронирование должно быть создано (HTTP 201)");
        BookingResponseDto createdBooking = response.getBody();
        assertNotNull(createdBooking, "Ответ не должен быть пустым");
        assertNotNull(createdBooking.getId(), "Созданное бронирование должно иметь ID");

        // Сохраняем для последующего теста
        bookingId = createdBooking.getId();
    }

    @Test
    @Order(2)
    @DisplayName("Получение списка бронирований (GET /api/bookings)")
    void testGetAllBookings() {
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);
        ResponseEntity<BookingResponseDto[]> response =
                adminRestTemplate.getForEntity("/api/bookings", BookingResponseDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Запрос списка бронирований должен вернуть HTTP 200");
        BookingResponseDto[] bookings = response.getBody();
        assertNotNull(bookings, "Список бронирований не должен быть пустым");
        assertTrue(bookings.length > 0, "Должно быть хотя бы одно бронирование");
    }

    @Test
    @Order(3)
    @DisplayName("Невозможно создать бронирование с неправильными датами (checkIn >= checkOut)")
    void testCreateBookingInvalidDates() {
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);

        // Пытаемся создать бронирование с датой заезда >= дате выезда
        BookingRequestDto invalidRequest = new BookingRequestDto(
                LocalDate.of(2025, 6, 10),
                LocalDate.of(2025, 6, 5),  // раньше даты заезда
                roomId,
                adminUserId
        );

        ResponseEntity<String> response =
                adminRestTemplate.postForEntity("/api/bookings", invalidRequest, String.class);

        // Ожидаем 400 Bad Request
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "При неправильных датах должен вернуться HTTP 400");
    }

    @Test
    @Order(4)
    @DisplayName("Невозможно создать бронирование с пересекающимися датами")
    void testCreateBookingOverlap() {
        TestRestTemplate adminRestTemplate = restTemplate.withBasicAuth(ADMIN_USERNAME, ADMIN_PASSWORD);

        // Существующее бронирование: 2025-06-01 по 2025-06-05
        // Пытаемся создать бронирование, пересекающееся по датам, например 2025-06-04 по 2025-06-07
        BookingRequestDto overlapRequest = new BookingRequestDto(
                LocalDate.of(2025, 6, 4),
                LocalDate.of(2025, 6, 7),
                roomId,
                adminUserId
        );

        ResponseEntity<String> response =
                adminRestTemplate.postForEntity("/api/bookings", overlapRequest, String.class);

        // Ожидаем 400 Bad Request
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "При пересечении дат должно вернуться HTTP 400");
    }
}
