package com.example.pethelper.mapper;

import com.example.pethelper.dto.PetDto;
import com.example.pethelper.entity.Pet;

public class PetMapper {
    public static PetDto mapToPetDto(Pet pet){
        return new PetDto(
                pet.getPetId(),
                pet.getPetName(),
                pet.getPetAge(),
                pet.getPetBd(),
                pet.getPetType(),
                pet.getPetBreed(),
                pet.getPetHealth()
        );
    }

    public static Pet mapToPet(PetDto petDto) {
        return new Pet(
                petDto.getPetId(),
                petDto.getPetName(),
                petDto.getPetAge(),
                petDto.getPetBd(),
                petDto.getPetType(),
                petDto.getPetBreed(),
                petDto.getPetHealth()
        );
    }
}
