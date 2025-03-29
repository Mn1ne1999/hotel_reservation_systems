package com.example.hotelbooking.repository;

import com.example.hotelbooking.entity.Statistic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticRepository extends MongoRepository<Statistic, String> {
    // При необходимости можно добавить методы поиска по userId или eventType
}
