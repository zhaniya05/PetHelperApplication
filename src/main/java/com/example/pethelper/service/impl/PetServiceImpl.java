package com.example.pethelper.service.impl;

import com.example.pethelper.dto.PetDto;
import com.example.pethelper.entity.ActivityType;
import com.example.pethelper.entity.Pet;
import com.example.pethelper.entity.User;
import com.example.pethelper.exception.ResourceNotFoundException;
import com.example.pethelper.mapper.PetMapper;
import com.example.pethelper.repository.PetRepository;
import com.example.pethelper.repository.UserRepository;
import com.example.pethelper.service.PetService;
import com.example.pethelper.service.UserActivityService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private static final Logger log = LoggerFactory.getLogger(PetServiceImpl.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${dog.api.key}")
    private String dogApiKey;

    @Value("${cat.api.key}")
    private String catApiKey;

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final UserActivityService userActivityService;
    private final ExperienceService experienceService;

    @Override
    public PetDto createPet(PetDto petDto) {
        User user = userRepository.findById(petDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + petDto.getUserId()));
        Pet pet = PetMapper.mapToPet(petDto, user);
        Pet savedPet = petRepository.save(pet);

        // ✅ ЛОГИРУЕМ СОЗДАНИЕ ПИТОМЦА
        userActivityService.logActivity(
                user,
                ActivityType.PET_CREATED,
                "Added new pet: " + petDto.getPetName(),
                "PET",
                savedPet.getPetId()
        );

        experienceService.awardExperience(user.getUserId(), "PET_CREATED");

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

        // Сохраняем старое имя для логирования
        String oldPetName = pet.getPetName();

        pet.setPetName(updatedPet.getPetName());
        pet.setPetAge(updatedPet.getPetAge());
        pet.setPetBd(updatedPet.getPetBd());
        pet.setPetType(updatedPet.getPetType());
        pet.setPetBreed(updatedPet.getPetBreed());
        pet.setPetHealth(updatedPet.getPetHealth());

        Pet updatedPetObj = petRepository.save(pet);

        // ✅ ИСПРАВЛЕННЫЙ СПОСОБ ПОИСКА ПОЛЬЗОВАТЕЛЯ
        User user = userRepository.findById(updatedPet.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + updatedPet.getUserId()));

        userActivityService.logActivity(
                user,
                ActivityType.PET_UPDATED,
                "Updated pet: " + oldPetName + " → " + updatedPet.getPetName(),
                "PET",
                petId
        );

        return PetMapper.mapToPetDto(updatedPetObj);
    }
    @Override
    @Transactional
    public void deletePet(Long petId) {
        // ✅ СНАЧАЛА НАЙДЕМ ПИТОМЦА
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + petId));


        User user = userRepository.findById(pet.getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // ✅ ЛОГИРУЕМ УДАЛЕНИЕ ПИТОМЦА
        userActivityService.logActivity(
                user,
                ActivityType.PET_DELETED,
                "Deleted pet: " + pet.getPetName(),
                "PET",
                petId
        );

        petRepository.deleteById(petId);
        petRepository.flush();
    }


    @Override
    public List<PetDto> getPetsByUser(Long userId) {
        return petRepository.findByUserUserId(userId)
                .stream()
                .map(PetMapper::mapToPetDto)
                .collect(Collectors.toList());
    }

//    @Override
//    public List<Map<String, Object>> getBreedsByType(String type) {
//        String url = null;
//
//        if (type.equalsIgnoreCase("dog")) {
//            url = "https://api.thedogapi.com/v1/breeds";
//        } else if (type.equalsIgnoreCase("cat")) {
//            url = "https://api.thecatapi.com/v1/breeds";
//        }
//
//        if (url == null) return List.of();
//
//        return Arrays.asList(restTemplate.getForObject(url, Map[].class));
//    }

    @Override
    public List<Map<String, Object>> getBreedsByType(String type) {
        String url = null;

        if (type.equalsIgnoreCase("dog")) {
            url = "https://api.thedogapi.com/v1/breeds";
        } else if (type.equalsIgnoreCase("cat")) {
            url = "https://api.thecatapi.com/v1/breeds";
        }

        if (url == null) return List.of();

        HttpHeaders headers = new HttpHeaders();
        if (type.equalsIgnoreCase("dog") && dogApiKey != null && !dogApiKey.isEmpty()) {
            headers.set("x-api-key", dogApiKey);
        } else if (type.equalsIgnoreCase("cat") && catApiKey != null && !catApiKey.isEmpty()) {
            headers.set("x-api-key", catApiKey);
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map[].class
        );

        return Arrays.asList(response.getBody());
    }


}
