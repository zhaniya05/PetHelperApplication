package com.example.pethelper.mapper;

import com.example.pethelper.dto.PetDto;
import com.example.pethelper.entity.Pet;
import com.example.pethelper.entity.User;

import java.util.ArrayList;
import java.util.Locale;

public class PetMapper {
    public static PetDto mapToPetDto(Pet pet){
        return new PetDto(
                pet.getPetId(),
                pet.getPetName(),
                pet.getPetAge(),
                pet.getPetBd(),
                pet.getPetType(),
                pet.getPetBreed(),
                pet.getPetHealth(),
                pet.getUser() != null ? pet.getUser().getUserId() : null
        );
    }

    public static Pet mapToPet(PetDto petDto, User user) {
        return new Pet(
                petDto.getPetId(),
                petDto.getPetName(),
                petDto.getPetAge(),
                petDto.getPetBd(),
                petDto.getPetType(),
                petDto.getPetBreed(),
                petDto.getPetHealth(),
                user
        );
    }
}
