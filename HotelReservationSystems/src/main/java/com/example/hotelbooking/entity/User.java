package com.example.hotelbooking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Уникальное имя пользователя, обязательное поле
    @Column(unique = true, nullable = false)
    private String username;

    // Пароль (обязательный)
    @Column(nullable = false)
    private String password;

    // Уникальный email, обязательное поле
    @Column(unique = true, nullable = false)
    private String email;

    // Роль пользователя (USER или ADMIN)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
