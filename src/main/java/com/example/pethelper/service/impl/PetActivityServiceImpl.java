package com.example.pethelper.service.impl;

import com.example.pethelper.dto.PetActivityDto;
import com.example.pethelper.entity.Pet;
import com.example.pethelper.entity.PetActivity;
import com.example.pethelper.entity.PetActivityType;
import com.example.pethelper.exception.ResourceNotFoundException;
import com.example.pethelper.mapper.PetActivityMapper;
import com.example.pethelper.repository.PetActivityRepository;
import com.example.pethelper.repository.PetRepository;
import com.example.pethelper.service.PetActivityService;
import com.example.pethelper.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class PetActivityServiceImpl implements PetActivityService {

    private final PetActivityRepository activityRepository;
    private final PetRepository petRepository;

    // OLD ADD
    @Override
    public PetActivityDto addActivity(PetActivityDto dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));

        PetActivity activity = PetActivityMapper.toEntity(dto, pet);
        PetActivity saved = activityRepository.save(activity);

        return PetActivityMapper.toDto(saved);
    }

    // NEW ADD (extended)
    @Override
    public PetActivityDto createAdvancedActivity(PetActivityDto dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));

        PetActivity entity = PetActivityMapper.toEntity(dto, pet);

        if (dto.getDate() == null) {
            dto.setDate(LocalDate.now());
        }

        // Ensure date is never null
        if (entity.getDate() == null) {
            entity.setDate(LocalDate.now());
        }

        if (entity.getDurationMinutes() != null && entity.getTime() != null) {
            entity.setEndTime(entity.getTime().plusMinutes(entity.getDurationMinutes()));
        }

        PetActivity saved = activityRepository.save(entity);
        return PetActivityMapper.toDto(saved);
    }

    @Override
    public List<PetActivityDto> getActivitiesForPet(Long petId) {
        return activityRepository.findByPetPetIdOrderByDateAscTimeAsc(petId)
                .stream().map(PetActivityMapper::toDto)
                .toList();
    }

    @Override
    public void deleteActivity(Long activityId) {
        activityRepository.deleteById(activityId);
    }


    @Override
    public List<PetActivityDto> getActivitiesForDate(Long petId, LocalDate date) {
        return activityRepository.findByPetPetIdOrderByDateAscTimeAsc(petId)
                .stream()
                .filter(a -> a.getDate() != null && a.getDate().equals(date))
                .map(PetActivityMapper::toDto)
                .toList();
    }


    @Override
    public int getDailyCalories(Long petId, LocalDate date) {
        return activityRepository.findByPetPetIdOrderByDateAscTimeAsc(petId)
                .stream()
                .filter(a -> a.getType() == PetActivityType.NUTRITION)
                .filter(a -> a.getDate() != null && a.getDate().equals(date)) // Added null check
                .mapToInt(a -> a.getCalories() == null ? 0 : a.getCalories())
                .sum();
    }


    @Override
    public int getDailyActivityMinutes(Long petId, LocalDate date) {
        return activityRepository.findByPetPetIdOrderByDateAscTimeAsc(petId)
                .stream()
                .filter(a -> a.getType() != PetActivityType.NUTRITION)
                .filter(a -> a.getDate() != null && a.getDate().equals(date)) // Added null check
                .mapToInt(a -> a.getDurationMinutes() == null ? 0 : a.getDurationMinutes())
                .sum();
    }
}
