package com.example.hotelbooking.repository;

import com.example.hotelbooking.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    // CRUD-операции будут доступны "из коробки": save, findById, findAll, deleteById и т.д.
}
