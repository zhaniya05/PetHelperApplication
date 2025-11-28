package com.example.pethelper.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="MySequenceGenerator")
    @SequenceGenerator(allocationSize=1, schema="public",  name="MySequenceGenerator", sequenceName = "mysequence")
    private Long userId;

    private String userName;
    private String email;
    private String password;
    private String role = "ROLE_USER";
    private Integer experiencePoints = 0;
    private Integer level = 1;

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    List<Pet> pets;

    @OneToMany(mappedBy="user", fetch=FetchType.LAZY)
    List<Post> posts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    List<Comment> comments;

    private String profilePicture;

    @OneToMany(mappedBy = "follower")
    private List<Follow> followingList;

    @OneToMany(mappedBy = "following")
    private List<Follow> followersList;

    @ManyToMany
    @JoinTable(
            name = "user_followed_tags",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> followedTags = new HashSet<>();

    public void addExperience(int points) {
        if (this.experiencePoints == null) {
            this.experiencePoints = 0;
        }
        if (this.level == null) {
            this.level = 1;
        }

        this.experiencePoints += points;
        checkLevelUp();
    }

    private void checkLevelUp() {
        while (canLevelUp()) {
            levelUp();
        }
    }

    private boolean canLevelUp() {
        if (this.level >= 50) return false;
        return this.experiencePoints >= getXpRequiredForCurrentLevel();
    }

    private void levelUp() {
        if (this.level >= 50) return;

        int xpUsed = getXpRequiredForCurrentLevel();
        this.experiencePoints -= xpUsed;
        this.level++;
        System.out.println("User " + userName + " reached level " + level + "! Used " + xpUsed + " XP");
        checkLevelUp();
    }

    public int getXpRequiredForCurrentLevel() {
        if (this.level == null || this.level >= 50) return 0;
        return 50 + 15 * (this.level - 1);
    }

    // ✅ ПРОГРЕСС ТЕКУЩЕГО УРОВНЯ
    @Transient
    public double getLevelProgress() {
        if (this.level == null || this.experiencePoints == null || this.level >= 50) {
            return 100.0;
        }

        int xpRequired = getXpRequiredForCurrentLevel();
        double progress = ((double) this.experiencePoints / xpRequired) * 100;
        return Math.min(100, Math.max(0, progress));
    }

    // ✅ ОСТАЛОСЬ XP ДО СЛЕДУЮЩЕГО УРОВНЯ
    @Transient
    public int getXpToNextLevel() {
        if (this.level == null || this.experiencePoints == null || this.level >= 50) {
            return 0;
        }

        int xpRequired = getXpRequiredForCurrentLevel();
        return Math.max(0, xpRequired - this.experiencePoints);
    }

    // ✅ МАКСИМАЛЬНЫЙ УРОВЕНЬ
    @Transient
    public boolean isMaxLevel() {
        return this.level != null && this.level >= 50;
    }

    // ✅ ДОБАВЛЯЕМ МЕТОДЫ ДЛЯ РАНГОВ
    @Transient
    public String getRank() {
        if (this.level == null) return "Couch Potato Owner";
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

    @Transient
    public String getRankIcon() {
        switch (getRank()) {
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

    @Transient
    public String getRankColor() {
        switch (getRank()) {
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

    @Transient
    public String getRankDescription() {
        switch (getRank()) {
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