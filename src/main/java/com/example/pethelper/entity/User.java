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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

//   @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//   List<Notification> notifications;


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
        // ✅ ОБЕСПЕЧИВАЕМ ИНИЦИАЛИЗАЦИЮ ПЕРЕД ИСПОЛЬЗОВАНИЕМ
        if (this.experiencePoints == null) {
            this.experiencePoints = 0;
        }
        if (this.level == null) {
            this.level = 1;
        }

        this.experiencePoints += points;
        checkLevelUp(); // ✅ ВЫЗЫВАЕМ ПРОВЕРКУ УРОВНЯ
    }

    private void checkLevelUp() {
        while (canLevelUp()) {
            levelUp();
        }
    }

    private boolean canLevelUp() {
        if (this.level >= 50) return false; // Максимальный уровень
        return this.experiencePoints >= getXpRequiredForCurrentLevel();
    }

    private void levelUp() {
        if (this.level >= 50) return; // Нельзя повысить выше 50

        int xpUsed = getXpRequiredForCurrentLevel();
        this.experiencePoints -= xpUsed;
        this.level++;
        System.out.println("User " + userName + " reached level " + level + "! Used " + xpUsed + " XP");

        // ✅ ПРОВЕРЯЕМ, МОЖЕТ БЫТЬ МОЖНО ПОВЫСИТЬ ЕЩЕ РАЗ (если XP много)
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

    // ✅ МАКСИМАЛЬНЫЙ УРОВЕНЬ (ТОЛЬКО ОДИН РАЗ!)
    @Transient
    public boolean isMaxLevel() {
        return this.level != null && this.level >= 50;
    }
}
