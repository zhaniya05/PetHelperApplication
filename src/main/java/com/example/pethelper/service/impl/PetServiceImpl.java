package com.example.pethelper.service.impl;

import com.example.pethelper.dto.PetDto;
import com.example.pethelper.entity.Pet;
import com.example.pethelper.entity.User;
import com.example.pethelper.exception.ResourceNotFoundException;
import com.example.pethelper.mapper.PetMapper;
import com.example.pethelper.repository.PetRepository;
import com.example.pethelper.repository.UserRepository;
import com.example.pethelper.service.PetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PetServiceImpl implements PetService {

    private PetRepository petRepository;
    private UserRepository userRepository;

    @Override
    public PetDto createPet(PetDto petDto) {
        User user = userRepository.findById(petDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + petDto.getUserId()));
        Pet pet = PetMapper.mapToPet(petDto, user);
        Pet savedPet = petRepository.save(pet);
        return PetMapper.mapToPetDto(savedPet);
    }

    @Override
    public PetDto getPetById(Long petId) {

        Pet pet = petRepository.findById(petId).orElseThrow(() ->
                new ResourceNotFoundException("Pet does not exist with a given ID: " + petId));
        return PetMapper.mapToPetDto(pet);
    }

    @Override
    public List<PetDto> getAllPets() {
        List<Pet> pets = petRepository.findAll();
        return pets.stream().map((pet) -> PetMapper.mapToPetDto(pet))
                .collect(Collectors.toList());
    }

    @Override
    public PetDto updatePet(Long petId, PetDto updatedPet) {
        Pet pet = petRepository.findById(petId).orElseThrow(() ->
                new ResourceNotFoundException("Pet does not exist by given id" + petId));
        pet.setPetName(updatedPet.getPetName());
        pet.setPetAge(updatedPet.getPetAge());
        pet.setPetBd(updatedPet.getPetBd());
        pet.setPetType(updatedPet.getPetType());
        pet.setPetBreed(updatedPet.getPetBreed());
        pet.setPetHealth(updatedPet.getPetHealth());

        Pet updatedPetObj = petRepository.save(pet);
        return PetMapper.mapToPetDto(updatedPetObj);
    }

    @Override
    public void deletePet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));
        petRepository.delete(pet);
    }


    @Override
    public List<PetDto> getPetsByUser(Long userId) {
        return petRepository.findByUserUserId(userId)
                .stream()
                .map(PetMapper::mapToPetDto)
                .collect(Collectors.toList());
    }
}
