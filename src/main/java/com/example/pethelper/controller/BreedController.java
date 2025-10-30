package com.example.pethelper.controller;

import com.example.pethelper.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BreedController {

    private final PetService petService;

    @GetMapping("/breeds")
    public List<Map<String, Object>> getBreeds(@RequestParam String type) {
        return petService.getBreedsByType(type);
    }
}
