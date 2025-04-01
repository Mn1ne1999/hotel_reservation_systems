package com.example.hotelbooking.service;

import com.example.hotelbooking.entity.Statistic;
import com.example.hotelbooking.repository.StatisticRepository;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.List;

@Service
public class CsvExportService {

    private final StatisticRepository statisticRepository;

    public CsvExportService(StatisticRepository statisticRepository) {
        this.statisticRepository = statisticRepository;
    }

    public void exportStatisticsToCsv(PrintWriter writer) {
        List<Statistic> stats = statisticRepository.findAll();
        // Записываем заголовок CSV
        writer.println("id,eventType,userId,details");
        // Записываем данные
        for (Statistic stat : stats) {
            writer.println(String.format("%s,%s,%s,%s",
                    stat.getId(),
                    stat.getEventType(),
                    stat.getUserId(),
                    stat.getDetails()));
        }
    }
}
