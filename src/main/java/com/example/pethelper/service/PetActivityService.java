package com.example.pethelper.service;

import com.example.pethelper.dto.PetActivityDto;

import java.util.List;

public interface PetActivityService {
    PetActivityDto addActivity(PetActivityDto dto);

    List<PetActivityDto> getActivitiesForPet(Long petId);

    void deleteActivity(Long activityId);
}

