package com.example.pethelper.mapper;

import com.example.pethelper.dto.PetActivityDto;
import com.example.pethelper.entity.Pet;
import com.example.pethelper.entity.PetActivity;

public class PetActivityMapper {


    public static PetActivityDto toDto(PetActivity a) {
        return new PetActivityDto(
                a.getId(),
                a.getDate(),
                a.getTime(),
                a.getEndTime(),
                a.getNote(),
                a.getType(),
                a.getAmountGrams(),
                a.getCalories(),
                a.getDurationMinutes(),
                a.getPet().getPetId()
        );
    }

    public static PetActivity toEntity(PetActivityDto dto, Pet pet) {
        PetActivity a = new PetActivity();
        a.setId(dto.getId());
        a.setDate(dto.getDate());
        a.setTime(dto.getTime());
        a.setNote(dto.getNote());
        a.setType(dto.getType());
        a.setAmountGrams(dto.getAmountGrams());
        a.setCalories(dto.getCalories());
        a.setDurationMinutes(dto.getDurationMinutes());
        a.setPet(pet);

        if (dto.getDurationMinutes() != null) {
            a.setEndTime(dto.getTime().plusMinutes(dto.getDurationMinutes()));
        }

        return a;
    }
}

