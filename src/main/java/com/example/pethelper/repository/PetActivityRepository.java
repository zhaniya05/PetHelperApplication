package com.example.pethelper.repository;

import com.example.pethelper.entity.PetActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

public interface PetActivityRepository extends JpaRepository<PetActivity, Long> {
    List<PetActivity> findByPetPetIdOrderByDateAscTimeAsc(Long petId);
}
