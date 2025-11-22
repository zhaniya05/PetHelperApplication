package com.example.pethelper.dto;

import com.example.pethelper.entity.PetActivityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetActivityDto {
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private LocalTime endTime;

    private String note;
    private PetActivityType type;

    private Integer amountGrams;
    private Integer calories;
    private Integer durationMinutes;

    private Long petId;
}

