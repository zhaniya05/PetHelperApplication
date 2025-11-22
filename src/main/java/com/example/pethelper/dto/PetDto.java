package com.example.pethelper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetDto {
    private Long petId;
    private String petName;
    private int petAge;
    private LocalDate petBd;
    private String petType;
    private String petBreed;
    private String petHealth;
    private Long userId;
    private Integer recommendedDailyCalories;
    private Integer recommendedDailyActivityMinutes;

}
