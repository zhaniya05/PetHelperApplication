package com.example.pethelper.controller;

import com.example.pethelper.dto.PetDto;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.User;
import com.example.pethelper.service.PetService;
import com.example.pethelper.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/pets")
@AllArgsConstructor
public class PetController {

    private final PetService petService;
    private final UserService userService;


    @GetMapping("/add/pet")
    public String showAddPetForm() {
        return "pet-form";
    }

    @PostMapping("/add/pet")
    public String addPet(Authentication authentication,
                              @RequestParam String name,
                              @RequestParam int age,
                              @RequestParam LocalDate birthday,
                              @RequestParam String breed,
                              @RequestParam String type){
        String email = authentication.getName();
        UserDto user = userService.findByEmail(email);
        PetDto p = new PetDto();
        p.setPetName(name);
        p.setPetAge(age);
        p.setPetBd(birthday);
        p.setPetBreed(breed);
        p.setPetType(type);
        p.setUserId(user.getUserId());
        petService.createPet(p);

        return "redirect:/pets/main";
    }


    @GetMapping("/main")
    public String getUserPets(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String type,
                              @RequestParam(required = false) String sort,
                              Model model,
                              Authentication authentication) {
        String email = authentication.getName();
        UserDto user = userService.findByEmail(email);

        List<PetDto> pets;

        if ("ROLE_ADMIN".equals(user.getRole())) {
            pets = petService.getAllPets();
        } else {
            pets = petService.getPetsByUser(user.getUserId());
        }

        // 🔍 Поиск
        if (keyword != null && !keyword.isBlank()) {
            pets = pets.stream()
                    .filter(p -> (p.getPetName() != null && p.getPetName().toLowerCase().contains(keyword.toLowerCase())) ||
                            (p.getPetBreed() != null && p.getPetBreed().toLowerCase().contains(keyword.toLowerCase())))
                    .toList();
        }

        // 🐾 Фильтрация по типу
        if (type != null && !type.isBlank()) {
            pets = pets.stream()
                    .filter(p -> p.getPetType() != null && p.getPetType().equalsIgnoreCase(type))
                    .toList();
        }

        // 🔢 Сортировка
        if (sort != null && !sort.isBlank()) {
            switch (sort) {
                case "name" -> pets = pets.stream()
                        .sorted((a, b) -> a.getPetName().compareToIgnoreCase(b.getPetName()))
                        .toList();
                case "age" -> pets = pets.stream()
                        .sorted((a, b) -> Integer.compare(a.getPetAge(), b.getPetAge()))
                        .toList();
                case "birthday" -> pets = pets.stream()
                        .sorted((a, b) -> b.getPetBd().compareTo(a.getPetBd()))
                        .toList();
            }
        }

        model.addAttribute("listPets", pets);
        model.addAttribute("user", user);

        // Чтобы форма сохраняла значения после отправки
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        model.addAttribute("sort", sort);

        return "main";
    }

    @GetMapping("/delete/{id}")
    public String deletePet(@PathVariable("id") Long petId) {
        System.out.println("🔄 DELETE: Attempting to delete pet with ID: " + petId);
        try {
            petService.deletePet(petId);
            System.out.println("✅ DELETE: Successfully deleted pet with ID: " + petId);
            return "redirect:/pets/main";
        } catch (Exception e) {
            System.out.println("❌ DELETE: Error deleting pet: " + e.getMessage());
            return "redirect:/pets/main?error=true";
        }
    }


    @GetMapping("/edit/{id}")
    public String showEditPetForm(@PathVariable("id") Long petId, Model model) {
        PetDto pet = petService.getPetById(petId);
        model.addAttribute("pet", pet);
        return "edit-pet";
    }

    @PostMapping("/update/{id}")
    public String updatePet(@PathVariable("id") Long petId,
                            @ModelAttribute("pet") PetDto updatedPet) {
        petService.updatePet(petId, updatedPet);
        return "redirect:/pets/main";
    }

}
