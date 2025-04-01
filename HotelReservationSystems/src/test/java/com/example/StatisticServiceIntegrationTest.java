package com.example;

import com.example.hotelbooking.entity.Statistic;
import com.example.hotelbooking.repository.StatisticRepository;
import com.example.hotelbooking.service.StatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Активируем тестовый профиль, в котором используется Embedded MongoDB без аутентификации
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Интеграционные тесты для сервиса статистики")
public class StatisticServiceIntegrationTest {

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private StatisticRepository statisticRepository;

    @BeforeEach
    @DisplayName("Очистка коллекции статистики перед тестами")
    public void setUp() {
        try {
            statisticRepository.deleteAll();
        } catch (Exception e) {
            System.err.println("Ошибка очистки коллекции статистики: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Сохранение и извлечение статистики")
    public void testSaveAndRetrieveStatistic() {
        Statistic stat = new Statistic(null, 100L, "USER_REGISTRATION", "{\"info\":\"Тест регистрации\"}");
        Statistic savedStat = statisticService.saveStatistic(stat);
        assertNotNull(savedStat.getId(), "Сохраненная статистика должна иметь ID");

        Statistic retrievedStat = statisticRepository.findById(savedStat.getId()).orElse(null);
        assertNotNull(retrievedStat, "Статистика должна быть извлечена");
        assertEquals("USER_REGISTRATION", retrievedStat.getEventType(), "Тип события должен совпадать");
        assertEquals(100L, retrievedStat.getUserId(), "ID пользователя должен совпадать");
        assertEquals("{\"info\":\"Тест регистрации\"}", retrievedStat.getDetails(), "Детали должны совпадать");
    }
}
