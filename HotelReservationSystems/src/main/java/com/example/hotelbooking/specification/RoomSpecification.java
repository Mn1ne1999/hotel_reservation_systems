package com.example.hotelbooking.specification;

import com.example.hotelbooking.entity.Room;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RoomSpecification {

    public static Specification<Room> getRoomsByFilter(
            final Long roomId,
            final String name,
            final Double minPrice,
            final Double maxPrice,
            final Integer maxGuests,
            final LocalDate checkInDate,
            final LocalDate checkOutDate,
            final Long hotelId
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (roomId != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), roomId));
            }
            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (maxGuests != null) {
                predicates.add(criteriaBuilder.equal(root.get("maxGuests"), maxGuests));
            }
            if (hotelId != null) {
                predicates.add(criteriaBuilder.equal(root.get("hotel").get("id"), hotelId));
            }

            // Фильтрация по датам: если заданы обе даты,
            // нужно исключить комнаты, которые недоступны в этот промежуток.
            // Здесь предполагается, что у Room есть коллекция unavailableDates или аналогичная логика,
            // которая может быть реализована через подзапрос или дополнительное условие.
            // Если заполнено только одно из полей — фильтрация не срабатывает.
            if (checkInDate != null && checkOutDate != null) {
                // Пример условия: комната считается свободной, если нет пересечений с датами unavailableDates.
                // Дополнительная логика должна быть реализована в зависимости от структуры Room.
                // Здесь добавляем заглушку: условие всегда true (или можно исключить комнаты по наличию бронирования в этот период).
                // Для примера: оставим пустой блок, если у вас нет соответствующей реализации.
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
