package com.example.hotelbooking.service;

import com.example.hotelbooking.entity.Statistic;
import com.example.hotelbooking.repository.StatisticRepository;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {

    private final StatisticRepository statisticRepository;

    public StatisticService(StatisticRepository statisticRepository) {
        this.statisticRepository = statisticRepository;
    }

    public Statistic saveStatistic(Statistic statistic) {
        return statisticRepository.save(statistic);
    }
}
