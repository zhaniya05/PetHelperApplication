package com.example.pethelper.controller;

import com.example.pethelper.dto.PetDto;
import com.example.pethelper.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private PetService petService;


    @PostMapping
    public ResponseEntity<PetDto> createPet(@RequestBody PetDto petDto) {
        PetDto savePet = petService.createPet(petDto);
        return new ResponseEntity<>(savePet, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<PetDto> getPetById(@RequestBody Long petId) {
        PetDto petDto = petService.getPetById(petId);
        return ResponseEntity.ok(petDto);
    }

    @GetMapping
    public ResponseEntity<List<PetDto>> getAllPets() {
        List<PetDto> pets = petService.getAllPets();
        return ResponseEntity.ok(pets);
    }


}
