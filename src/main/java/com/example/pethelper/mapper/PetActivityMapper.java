package com.example.pethelper.mapper;

import com.example.pethelper.dto.PetActivityDto;
import com.example.pethelper.entity.Pet;
import com.example.pethelper.entity.PetActivity;

public class PetActivityMapper {

    public static PetActivityDto toDto(PetActivity activity) {
        return new PetActivityDto(
                activity.getId(),
                activity.getDate(),
                activity.getTime(),
                activity.getNote(),
                activity.getPet().getPetId()
        );
    }

    public static PetActivity toEntity(PetActivityDto dto, Pet pet) {
        return new PetActivity(
                dto.getId(),
                dto.getDate(),
                dto.getTime(),
                dto.getNote(),
                pet
        );
    }
}

