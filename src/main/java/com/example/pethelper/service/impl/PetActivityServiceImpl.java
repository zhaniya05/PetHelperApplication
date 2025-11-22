package com.example.pethelper.service.impl;

import com.example.pethelper.dto.PetActivityDto;
import com.example.pethelper.entity.Pet;
import com.example.pethelper.entity.PetActivity;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetActivityServiceImpl implements PetActivityService {

    private final PetActivityRepository activityRepository;
    private final PetRepository petRepository;

    @Override
    public PetActivityDto addActivity(PetActivityDto dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));

        PetActivity activity = PetActivityMapper.toEntity(dto, pet);
        PetActivity saved = activityRepository.save(activity);

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
}

