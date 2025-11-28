package com.example.pethelper.dto;

import com.example.pethelper.entity.Pet;
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
    private String rank;

    public void calculateProgress() {
        if (level == null || experiencePoints == null) {
            this.levelProgress = 0.0;
            this.xpToNextLevel = 50;
            this.rank = getRankForLevel(1);
            return;
        }

        if (level >= 50) {
            this.levelProgress = 100.0;
            this.xpToNextLevel = 0;
            this.rank = getRankForLevel(50);
            return;
        }

        int xpRequired = 50 + 15 * (level - 1);
        this.levelProgress = Math.min(100, ((double) experiencePoints / xpRequired) * 100);
        this.xpToNextLevel = Math.max(0, xpRequired - experiencePoints);
        this.rank = getRankForLevel(this.level);
    }

    private String getRankForLevel(int level) {
        if (level >= 45) return "Mythical Beast Tamer";
        if (level >= 40) return "Ultimate Pet Guardian";
        if (level >= 35) return "Legendary Caretaker";
        if (level >= 30) return "Pet Paradise Creator";
        if (level >= 25) return "Animal Soulmate";
        if (level >= 20) return "Super Pet Parent";
        if (level >= 15) return "Dedicated Pet Lover";
        if (level >= 10) return "Good Enough Owner";
        if (level >= 5) return "Part-Time Pet Parent";
        return "Couch Potato Owner";
    }

    // ✅ ДОБАВЛЯЕМ МЕТОДЫ ДЛЯ РАНГОВ В DTO
    public String getRankColor() {
        switch (this.rank) {
            case "Mythical Beast Tamer": return "#FFD700"; // золотой
            case "Ultimate Pet Guardian": return "#C0C0C0"; // серебряный
            case "Legendary Caretaker": return "#FF6B35"; // оранжевый
            case "Pet Paradise Creator": return "#9C27B0"; // фиолетовый
            case "Animal Soulmate": return "#2196F3"; // синий
            case "Super Pet Parent": return "#4CAF50"; // зеленый
            case "Dedicated Pet Lover": return "#00BCD4"; // бирюзовый
            case "Good Enough Owner": return "#FF9800"; // янтарный
            case "Part-Time Pet Parent": return "#795548"; // коричневый
            default: return "#607D8B"; // серо-голубой
        }
    }

    public String getRankIcon() {
        switch (this.rank) {
            case "Mythical Beast Tamer": return "🐉";
            case "Ultimate Pet Guardian": return "⚔️";
            case "Legendary Caretaker": return "💎";
            case "Pet Paradise Creator": return "👑";
            case "Animal Soulmate": return "⚜️";
            case "Super Pet Parent": return "🌸";
            case "Dedicated Pet Lover": return "❤️";
            case "Good Enough Owner": return "🐾";
            case "Part-Time Pet Parent": return "🍕";
            default: return "🥔";
        }
    }

    public String getRankDescription() {
        switch (this.rank) {
            case "Mythical Beast Tamer": return "Even dragons bring YOU their treasure (mostly hairballs)";
            case "Ultimate Pet Guardian": return "Your pet's Instagram has more followers than yours";
            case "Legendary Caretaker": return "Vets call YOU for advice... at 3 AM";
            case "Pet Paradise Creator": return "Your home has more pet amenities than a five-star hotel";
            case "Animal Soulmate": return "You understand 'meow' better than your native language";
            case "Super Pet Parent": return "You know the exact moment the food bowl becomes 'empty'";
            case "Dedicated Pet Lover": return "Your phone is 90% pet photos and 10% accidental selfies";
            case "Good Enough Owner": return "The pet is alive... that counts, right?";
            case "Part-Time Pet Parent": return "You remember to feed them... when they remind you";
            default: return "Pet? Oh right... there was one somewhere...";
        }
    }
}