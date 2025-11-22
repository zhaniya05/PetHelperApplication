package com.example.pethelper.controller;

import com.example.pethelper.dto.PetActivityDto;
import com.example.pethelper.entity.PetActivity;
import com.example.pethelper.entity.PetActivityType;
import com.example.pethelper.service.PetActivityService;
import com.example.pethelper.service.PetService;
import com.example.pethelper.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("/pets/{petId}/activities")
@RequiredArgsConstructor
public class PetActivityController {

    private final PetActivityService activityService;
    private final PetService petService;

    // -----------------------------------------------------
    // MAIN PAGE — only today unless date is passed manually
    // -----------------------------------------------------
    @GetMapping
    public String viewActivities(@PathVariable Long petId,
                                 @RequestParam(required = false) LocalDate date,
                                 Model model) {

        if (date == null) date = LocalDate.now();

        var pet = petService.getPetById(petId);

        model.addAttribute("pet", pet);
        model.addAttribute("date", date);

        model.addAttribute("activities",
                activityService.getActivitiesForDate(petId, date));

        // today's stats
        int dailyCalories = activityService.getDailyCalories(petId, date);
        int dailyMinutes = activityService.getDailyActivityMinutes(petId, date);

        int recommendedCalories = pet.getRecommendedDailyCalories();
        int recommendedMinutes = pet.getRecommendedDailyActivityMinutes();

        model.addAttribute("dailyCalories", dailyCalories);
        model.addAttribute("dailyMinutes", dailyMinutes);
        model.addAttribute("recommendedCalories", recommendedCalories);
        model.addAttribute("recommendedMinutes", recommendedMinutes);

        // Calculate percentages
        Map<String, Integer> percentages = calculatePercentages(
                dailyCalories, recommendedCalories,
                dailyMinutes, recommendedMinutes
        );

        model.addAttribute("nutritionPercentage", percentages.get("nutritionPercentage"));
        model.addAttribute("activityPercentage", percentages.get("activityPercentage"));
        model.addAttribute("fatsPercentage", percentages.get("fatsPercentage"));
        model.addAttribute("proteinsPercentage", percentages.get("proteinsPercentage"));
        model.addAttribute("carbsPercentage", percentages.get("carbsPercentage"));

        // new activity DTO already has today's date
        PetActivityDto dto = new PetActivityDto();
        dto.setDate(date);
        model.addAttribute("newActivity", dto);

        return "pet-activities";
    }

    // Add this method to PetActivityController
    @GetMapping("/add")
    public String showAddActivityForm(@PathVariable Long petId, Model model) {
        var pet = petService.getPetById(petId);
        model.addAttribute("pet", pet);

        PetActivityDto dto = new PetActivityDto();
        dto.setDate(LocalDate.now());
        model.addAttribute("newActivity", dto);

        return "activity-create-form";
    }

    // Updated percentage calculation method
    private Map<String, Integer> calculatePercentages(int dailyCalories, int recommendedCalories,
                                                      int dailyMinutes, int recommendedMinutes) {
        Map<String, Integer> percentages = new HashMap<>();

        // Nutrition percentage
        int nutritionPercentage = recommendedCalories > 0 ?
                Math.min(100, (dailyCalories * 100) / recommendedCalories) : 0;

        // Activity percentage
        int activityPercentage = recommendedMinutes > 0 ?
                Math.min(100, (dailyMinutes * 100) / recommendedMinutes) : 0;

        percentages.put("nutritionPercentage", nutritionPercentage);
        percentages.put("activityPercentage", activityPercentage);

        // Simplified nutrient breakdown - you can make this more sophisticated later
        // For now, using fixed ratios as in your example
        percentages.put("fatsPercentage", Math.min(100, (int)(nutritionPercentage * 0.47)));
        percentages.put("proteinsPercentage", Math.min(100, (int)(nutritionPercentage * 0.67)));
        percentages.put("carbsPercentage", Math.min(100, (int)(nutritionPercentage * 0.43)));

        return percentages;
    }

    // -----------------------------------------------------
    // ADD NEW ACTIVITY — ALWAYS SAVES FOR TODAY (OR SELECTED DATE)
    // -----------------------------------------------------
    @PostMapping("/add")
    public String addActivity(@PathVariable Long petId,
                              @ModelAttribute("newActivity") PetActivityDto dto,
                              Model model) {

        dto.setPetId(petId);

        // если по какой-то причине пришёл null — ставим сегодня
        if (dto.getDate() == null) {
            dto.setDate(LocalDate.now());
        }

        activityService.createAdvancedActivity(dto);

        model.addAttribute("newActivity", new PetActivityDto());

        return "redirect:/pets/" + petId + "/activities?date=" + dto.getDate();
    }

    // -----------------------------------------------------
    // DELETE
    // -----------------------------------------------------
    @GetMapping("/delete/{id}")
    public String deleteActivity(@PathVariable Long petId,
                                 @PathVariable Long id,
                                 @RequestParam LocalDate date) {

        activityService.deleteActivity(id);

        // возвращаемся на тот же день
        return "redirect:/pets/" + petId + "/activities?date=" + date;
    }
}


