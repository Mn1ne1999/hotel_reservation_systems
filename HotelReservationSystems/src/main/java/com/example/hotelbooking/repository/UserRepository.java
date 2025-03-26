package com.example.hotelbooking.repository;

import com.example.hotelbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Поиск пользователя по имени
    Optional<User> findByUsername(String username);

    // Проверка существования пользователя по имени или email
    boolean existsByUsernameOrEmail(String username, String email);
}
