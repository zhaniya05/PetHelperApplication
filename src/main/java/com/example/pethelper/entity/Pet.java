package com.example.pethelper.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import com.example.pethelper.entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    private String petName;

    private int petAge;

    private LocalDate petBd;

    private String petType;

    private String petBreed;

    private String petHealth;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
