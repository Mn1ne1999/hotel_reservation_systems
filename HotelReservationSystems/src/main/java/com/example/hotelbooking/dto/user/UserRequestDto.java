package com.example.hotelbooking.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    private String username;
    private String password;
    private String email;
    // Роль передаётся как строка ("USER" или "ADMIN")
    private String role;
}
