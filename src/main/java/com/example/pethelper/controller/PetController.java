package com.example.pethelper.controller;

import com.example.pethelper.dto.PetDto;
import com.example.pethelper.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private PetService petService;


    @PostMapping
    public ResponseEntity<PetDto> createPet(@RequestBody PetDto petDto) {
        PetDto savePet = petService.createPet(petDto);
        return new ResponseEntity<>(savePet, HttpStatus.CREATED);
    }
}
