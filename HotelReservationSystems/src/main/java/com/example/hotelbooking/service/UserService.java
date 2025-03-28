package com.example.hotelbooking.service;

import com.example.hotelbooking.entity.User;
import com.example.hotelbooking.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Создание нового пользователя с проверкой на дубликаты
    public User createUser(User user) {
        if (userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким именем или email уже существует");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Получение пользователя по ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с id: " + id));
    }

    // Получение всех пользователей
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Обновление данных пользователя (при необходимости)
    public User updateUser(Long id, User userData) {
        User existingUser = getUserById(id);
        existingUser.setUsername(userData.getUsername());
        existingUser.setEmail(userData.getEmail());
        // ВАЖНО: повторное шифрование пароля, чтобы сохранить его в формате BCrypt
        existingUser.setPassword(passwordEncoder.encode(userData.getPassword()));
        existingUser.setRole(userData.getRole());
        return userRepository.save(existingUser);
    }


    // Удаление пользователя
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Пользователь не найден с id: " + id);
        }
        userRepository.deleteById(id);
        userRepository.flush();
    }
}
