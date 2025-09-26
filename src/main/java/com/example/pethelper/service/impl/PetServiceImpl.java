package com.example.pethelper.service.impl;

import com.example.pethelper.dto.PetDto;
import com.example.pethelper.entity.Pet;
import com.example.pethelper.mapper.PetMapper;
import com.example.pethelper.repository.PetRepository;
import com.example.pethelper.service.PetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PetServiceImpl implements PetService {

    private PetRepository petRepository;

    @Override
    public PetDto createPet(PetDto petDto) {

        Pet pet = PetMapper.mapToPet(petDto);
        Pet savePet = petRepository.save(pet);

        return PetMapper.mapToPetDto(savePet);
    }
}
