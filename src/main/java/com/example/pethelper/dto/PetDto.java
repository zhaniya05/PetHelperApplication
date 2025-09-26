package com.example.pethelper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetDto {
    private Long petId;
    private String petName;
    private int petAge;
    private Date petBd;
    private String petType;
    private String petBreed;
    private String petHealth;
}
