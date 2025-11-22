package com.example.pethelper.entity;

import jakarta.persistence.*;
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
@Entity
@Table(name = "pet_activities")
public class PetActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_date", nullable = false)
    private LocalDate date;

    private LocalTime time;

    private String note;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;
}