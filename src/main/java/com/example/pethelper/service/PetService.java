package com.example.pethelper.service;

import com.example.pethelper.dto.PetDto;

import java.util.List;

public interface PetService {
    PetDto createPet(PetDto petDto);
    PetDto getPetById(Long petId);
    List<PetDto> getAllPets();
    PetDto updatePet(Long petId, PetDto updatedPet);
    void deletePet(Long petId);
    List<PetDto> getPetsByUser(Long userId);

}
