package com.example.pethelper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_activity_history")
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activityType;

    private String description;

    private String targetEntity; // "POST", "COMMENT", "PET"
    private Long targetId; // ID целевой сущности

    private LocalDateTime activityDate;

    @PrePersist
    public void prePersist() {
        if (this.activityDate == null) {
            this.activityDate = LocalDateTime.now();
        }
    }
}
