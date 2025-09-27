package com.example.pethelper.repository;

import com.example.pethelper.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByUserUserId(Long userId);


}
