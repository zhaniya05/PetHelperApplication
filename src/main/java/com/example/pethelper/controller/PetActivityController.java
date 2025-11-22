package com.example.pethelper.controller;

import com.example.pethelper.dto.PetActivityDto;
import com.example.pethelper.entity.PetActivity;
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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pets/{petId}/activities")
@RequiredArgsConstructor
public class PetActivityController {

    private final PetActivityService activityService;
    private final PetService petService;

    @GetMapping
    public String viewActivities(@PathVariable Long petId, Model model) {
        model.addAttribute("pet", petService.getPetById(petId));
        model.addAttribute("activities", activityService.getActivitiesForPet(petId));
        model.addAttribute("newActivity", new PetActivityDto());
        return "pet-activities";
    }

    @PostMapping("/add")
    public String addActivity(@PathVariable Long petId,
                              @ModelAttribute("newActivity") PetActivityDto dto) {
        dto.setPetId(petId);
        activityService.addActivity(dto);
        return "redirect:/pets/" + petId + "/activities";
    }

    @GetMapping("/delete/{id}")
    public String deleteActivity(@PathVariable Long petId, @PathVariable Long id) {
        activityService.deleteActivity(id);
        return "redirect:/pets/" + petId + "/activities";
    }
}

