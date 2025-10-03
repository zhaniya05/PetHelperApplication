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
//
//    @GetMapping("{id}")
//    public ResponseEntity<PetDto> getPetById(@PathVariable("id") Long petId) {
//        PetDto petDto = petService.getPetById(petId);
//        return ResponseEntity.ok(petDto);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<PetDto>> getAllPets() {
//        List<PetDto> pets = petService.getAllPets();
//        return ResponseEntity.ok(pets);
//    }


    @GetMapping("/main")
    public String getUserPets(Model model, Authentication authentication) {
        String email = authentication.getName();
        UserDto user = userService.findByEmail(email);
        List<PetDto> pets = petService.getPetsByUser(user.getUserId());
        model.addAttribute("listPets", pets);
        model.addAttribute("user", user);
        return "main";
    }

    @GetMapping("/delete/{id}")
    public String deletePet(@PathVariable("id") Long petId) {
        petService.deletePet(petId);
        return "redirect:/pets/main";
    }


//
//    @PutMapping("{id}")
//    public ResponseEntity<PetDto> updatePet(@PathVariable("id") Long petId,
//                                            @RequestBody PetDto updatedPet) {
//        PetDto petDto = petService.updatePet(petId, updatedPet);
//        return ResponseEntity.ok(petDto);
//    }


}
