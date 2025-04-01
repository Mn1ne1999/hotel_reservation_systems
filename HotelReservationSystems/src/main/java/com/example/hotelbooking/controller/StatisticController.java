package com.example.hotelbooking.controller;

import com.example.hotelbooking.service.CsvExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestController
public class StatisticController {

    private final CsvExportService csvExportService;

    public StatisticController(CsvExportService csvExportService) {
        this.csvExportService = csvExportService;
    }

    // Эндпоинт для экспорта статистики, доступный только администратору
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/statistics/export")
    public ResponseEntity<String> exportStatistics() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        csvExportService.exportStatisticsToCsv(printWriter);
        String csvContent = stringWriter.toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"statistics.csv\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent);
    }
}
