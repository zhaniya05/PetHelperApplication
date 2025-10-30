package com.example.pethelper.service;

import com.example.pethelper.dto.*;

import java.time.LocalDate;
import java.util.Map;

public interface StatisticsService {
    StatisticsDto getPlatformStatistics();
    StatisticsDto getStatisticsForPeriod(LocalDate startDate, LocalDate endDate);
    Map<String, Object> getDashboardData();
}