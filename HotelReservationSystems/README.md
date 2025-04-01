
# Система бронирования отелей

Это итоговый проект курса «Разработка на Spring Framework». Проект представляет собой бэкенд-сервис для бронирования отелей с возможностью управления контентом через административную панель CMS.

## Основные возможности

- **Бронирование отелей и комнат:** пользователи могут бронировать отели и комнаты на определённые даты, а система проверяет доступность.
- **Поиск и фильтрация:** реализован постраничный вывод и динамическая фильтрация отелей и комнат по параметрам: название, город, адрес, расстояние до центра, цена, количество гостей и др.
- **Оценка отелей:** пользователи могут выставлять оценки отелям (от 1 до 5), а система динамически пересчитывает средний рейтинг.
- **Управление пользователями:** регистрация, аутентификация и CRUD-операции для пользователей с использованием Spring Security (Basic Auth).
- **Статистика:** сбор статистических данных о регистрации пользователей и бронировании комнат через Kafka. Статистика сохраняется в MongoDB, а реализован эндпоинт для экспорта данных в CSV (доступен только для администратора).

---

## Технологии

- **Java 17**
- **Spring Boot** (Spring MVC, Spring Data JPA, Spring Data MongoDB, Spring Security)
- **PostgreSQL** – основная реляционная база данных
- **H2** – in‑memory база для тестирования JPA
- **MongoDB** – для хранения статистических данных
- **Kafka** – для отправки и получения статистических событий
- **MapStruct** – для маппинга сущностей в DTO
- **Docker & Docker Compose** – для контейнеризации приложения, PostgreSQL, MongoDB, Kafka и ZooKeeper
- **Gradle** – система сборки

---

## Структура проекта

```
HotelBookingSystems
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.hotelbooking
│   │   │       ├── config           // Конфигурация (например, MongoConfig)
│   │   │       ├── controller       // REST-контроллеры (HotelController, RoomController, UserController, StatisticController и др.)
│   │   │       ├── dto              // DTO для обмена данными (HotelRequestDto, HotelResponseDto, PagedResponseDto, RatingUpdateRequestDto и др.)
│   │   │       ├── entity           // Сущности (Hotel, Room, User, Statistic и др.)
│   │   │       ├── kafka            // Сервисы для работы с Kafka (KafkaProducer, KafkaConsumer)
│   │   │       ├── mapper           // MapStruct мапперы (HotelMapper, RoomMapper и др.)
│   │   │       ├── repository       // Репозитории (HotelRepository, RoomRepository, UserRepository, StatisticRepository и др.)
│   │   │       └── service          // Сервисы (HotelService, RoomService, UserService, StatisticService и др.)
│   │   └── resources
│   │       ├── application.properties       // Основные настройки приложения
│   │       └── (другие файлы конфигурации)
│   └── test
│       ├── java
│       │   └── com.example              // Интеграционные тесты (HotelRatingIntegrationTest, RoomSearchIntegrationTest, StatisticServiceIntegrationTest и др.)
│       └── resources
│           └── application-test.properties  // Настройки для тестовой среды
├── docker-compose.yml
└── build.gradle.kts
```

---

## Установка и запуск

### 1. Клонирование репозитория

```bash
git clone <URL вашего репозитория>
cd <название проекта>
```

### 2. Запуск контейнеров через Docker Compose

Убедитесь, что у вас установлен Docker Desktop. В корневой папке проекта выполните:

```bash
docker-compose up -d
```

Если вы ранее запускали контейнеры и хотите переинициализировать данные (например, чтобы применить новые учетные данные для MongoDB), выполните:

```bash
docker-compose down -v
docker-compose up -d
```

### 3. Конфигурация подключения

**Основной профиль (application.properties):**

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/hotel_booking_db
spring.datasource.url=jdbc:postgresql://localhost:5432/hotel_booking_db
spring.datasource.username=postgres
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=create-drop
```

**Тестовый профиль (application-test.properties):**

```properties
spring.data.mongodb.uri=mongodb://admin:adminPass@localhost:27017/hotel_booking_db?authSource=admin
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.profiles.active=test
```

*Примечание:* Если вы используете Embedded MongoDB для тестирования, настройте строку подключения соответствующим образом.

### 4. Сборка и запуск приложения

Соберите проект:

```bash
./gradlew build
```

Запустите приложение:

```bash
./gradlew bootRun
```

Приложение будет доступно по адресу: [http://localhost:8080](http://localhost:8080)

---

## API

### Отели

- **Создание отеля:** `POST /api/hotels`
- **Получение отеля по ID:** `GET /api/hotels/{id}`
- **Получение списка отелей:** `GET /api/hotels`
- **Обновление отеля:** `PUT /api/hotels/{id}`
- **Удаление отеля:** `DELETE /api/hotels/{id}`
- **Обновление рейтинга отеля:** `POST /api/hotels/{id}/rating`
- **Постраничный поиск отелей:** `GET /api/hotels/paged`

### Комнаты

- **Создание комнаты:** `POST /api/rooms`
- **Получение комнаты по ID:** `GET /api/rooms/{id}`
- **Обновление комнаты:** `PUT /api/rooms/{id}`
- **Удаление комнаты:** `DELETE /api/rooms/{id}`
- **Поиск комнат с фильтрацией:** `GET /api/rooms/search`

### Пользователи

- **Регистрация пользователя:** `POST /api/users/register`
- *Другие операции (GET, PUT, DELETE) доступны с аутентификацией (Basic Auth).*

### Статистика

- **События:**
    - Отправка событий регистрации пользователя и бронирования комнаты через Kafka.
    - Сохранение статистических данных в MongoDB.
- **Экспорт статистики в CSV:** `GET /api/statistics/export` (доступно только для администратора).

---

## Тестирование

Запустите тесты с помощью Gradle:

```bash
./gradlew test
```

Тесты включают интеграционные проверки основных функций приложения.

---

## Контейнеризация

Пример файла **docker-compose.yml**:

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - mongodb

  postgres:
    image: postgres:13
    restart: always
    environment:
      POSTGRES_DB: hotel_booking_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: admin
    ports:
      - "5432:5432"

  mongodb:
    image: mongo:6.0
    container_name: mongo
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: adminPass
      MONGO_INITDB_DATABASE: hotel_booking_db

  mongo-express:
    image: mongo-express:latest
    container_name: mongo-express
    ports:
      - "8081:8081"
    environment:
      ME_CONFIG_MONGODB_ADMINUSERNAME: admin
      ME_CONFIG_MONGODB_ADMINPASSWORD: adminPass
      ME_CONFIG_MONGODB_SERVER: mongodb

  kafka:
    image: confluentinc/cp-kafka:latest
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
```
---
