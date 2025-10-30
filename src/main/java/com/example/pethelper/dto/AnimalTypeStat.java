package com.example.pethelper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnimalTypeStat {
    private String type;
    private Long count;
    private Double percentage;
}
