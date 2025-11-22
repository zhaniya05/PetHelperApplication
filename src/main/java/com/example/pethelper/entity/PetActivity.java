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

    private LocalTime endTime;

    private String note;

    @Enumerated(EnumType.STRING)
    private PetActivityType type;

    private Integer amountGrams;
    private Integer calories;

    private Integer durationMinutes;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDate.now();
        }
    }
}