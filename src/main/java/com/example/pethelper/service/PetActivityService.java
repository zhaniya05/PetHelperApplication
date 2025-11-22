package com.example.pethelper.service;

import com.example.pethelper.dto.PetActivityDto;

import java.time.LocalDate;
import java.util.List;

public interface PetActivityService {
    PetActivityDto addActivity(PetActivityDto dto);
    PetActivityDto createAdvancedActivity(PetActivityDto dto);
    List<PetActivityDto> getActivitiesForPet(Long petId);
    void deleteActivity(Long activityId);
    List<PetActivityDto> getActivitiesForDate(Long petId, LocalDate date);
    int getDailyCalories(Long petId, LocalDate date);
    int getDailyActivityMinutes(Long petId, LocalDate date);
}

