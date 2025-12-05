package com.example.pethelper.controller;

import com.example.pethelper.dto.StatisticsDto;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.service.StatisticsService;
import com.example.pethelper.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/statistics")
@AllArgsConstructor
public class StatisticsController {

   private final StatisticsService statisticsService;
   private final UserService userService;


    @GetMapping
    public String getStatisticsPage(Model model, Authentication authentication) {

        StatisticsDto statistics = statisticsService.getPlatformStatistics();
        model.addAttribute("stats", statistics);

        // Формируем списки для JS-графиков
        List<String> typeLabels = statistics.getTypeStats()
                .stream()
                .map(t -> t.getType()) // исправлено: t — элемент списка
                .collect(Collectors.toList());

        List<Long> typeData = statistics.getTypeStats()
                .stream()
                .map(t -> t.getCount()) // исправлено
                .collect(Collectors.toList());

        model.addAttribute("typeLabels", typeLabels);
        model.addAttribute("typeData", typeData);

        if (authentication != null && authentication.isAuthenticated()) {
            UserDto currentUser = userService.findByEmail(authentication.getName());
            model.addAttribute("user", currentUser);
        }

        return "stats";
    }



    // ✅ ДЛЯ DASHBOARD DATA (AJAX/API)
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> dashboardData = statisticsService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }

    // ✅ ДЛЯ ПЕРИОДА (AJAX/API)
    @GetMapping("/period")
    public ResponseEntity<StatisticsDto> getStatisticsForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        StatisticsDto statistics = statisticsService.getStatisticsForPeriod(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    // ✅ ЭКСПОРТ В CSV
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStatistics() {
        StatisticsDto statistics = statisticsService.getPlatformStatistics();
        String csvData = convertToCsv(statistics);
        byte[] csvBytes = csvData.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statistics.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(csvBytes);
    }

    private String convertToCsv(StatisticsDto stats) {
        StringBuilder csv = new StringBuilder();
        csv.append("Metric,Value\n");
        csv.append("Total Posts,").append(stats.getGeneralStats().getTotalPosts()).append("\n");
        csv.append("Total Comments,").append(stats.getGeneralStats().getTotalComments()).append("\n");
        csv.append("Total Pets,").append(stats.getGeneralStats().getTotalPets()).append("\n");
        csv.append("Total Users,").append(stats.getGeneralStats().getTotalUsers()).append("\n");
        csv.append("Total Likes,").append(stats.getGeneralStats().getTotalLikes()).append("\n");
        csv.append("Average Likes Per Post,").append(stats.getGeneralStats().getAverageLikesPerPost()).append("\n");
        return csv.toString();
    }
}