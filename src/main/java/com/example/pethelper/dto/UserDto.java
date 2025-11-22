package com.example.pethelper.dto;

import com.example.pethelper.entity.Pet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userId;

    private String userName;

    private String email;

    private String password;

    private String role;

    private List<Long> petIds;

    private String profilePicture;

    private String confirmPassword;

    private Integer experiencePoints;
    private Integer level;
    private Double levelProgress;
    private Integer xpToNextLevel;


    // dto/UserDto.java
    public void calculateProgress() {
        if (level == null || experiencePoints == null) {
            this.levelProgress = 0.0;
            this.xpToNextLevel = 50;
            return;
        }

        // ✅ МАКСИМАЛЬНЫЙ УРОВЕНЬ
        if (level >= 50) {
            this.levelProgress = 100.0;
            this.xpToNextLevel = 0;
            return;
        }

        int xpRequired = getXpRequiredForLevel(level);
        this.levelProgress = Math.min(100, ((double) experiencePoints / xpRequired) * 100);
        this.xpToNextLevel = Math.max(0, xpRequired - experiencePoints);
    }

    private int getXpRequiredForLevel(int currentLevel) {
        if (currentLevel >= 50) return 0;
        return 50 + 15 * (currentLevel - 1);
    }

    // ✅ ДЛЯ ОТОБРАЖЕНИЯ МАКСИМАЛЬНОГО УРОВНЯ
    @Transient
    public boolean isMaxLevel() {
        return level != null && level >= 50;
    }
}
