package com.example.pethelper.service.impl;


import com.example.pethelper.entity.User;
import com.example.pethelper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final UserRepository userRepository;

    // ✅ КОНФИГУРАЦИЯ НАГРАД ЗА ДЕЙСТВИЯ
    private final Map<String, Integer> experienceRewards = Map.of(
            "POST_CREATED", 15,
            "POST_LIKED", 5,
            "COMMENT_CREATED", 3,
            "PET_CREATED", 20,
            "PROFILE_COMPLETED", 10
    );

    @Transactional
    public void awardExperience(Long userId, String actionType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ ИНИЦИАЛИЗИРУЕМ ПОЛЯ ЕСЛИ ОНИ NULL
        initializeUserFields(user);

        Integer points = experienceRewards.getOrDefault(actionType, 0);

        if (points > 0) {
            int oldLevel = user.getLevel();
            user.addExperience(points);
            userRepository.save(user);

            // Логируем начисление опыта
            System.out.println("Awarded " + points + " XP to user " + userId +
                    " for action: " + actionType);

            // Проверяем, повысился ли уровень
            if (user.getLevel() > oldLevel) {
                System.out.println("User " + user.getUserName() + " leveled up to level " + user.getLevel());
                // Здесь можно добавить отправку уведомления
            }
        }
    }

    @Transactional
    public void awardCustomExperience(Long userId, Integer points) {
        if (points > 0) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ✅ ИНИЦИАЛИЗИРУЕМ ПОЛЯ ЕСЛИ ОНИ NULL
            initializeUserFields(user);

            user.addExperience(points);
            userRepository.save(user);
        }
    }

    // ✅ МЕТОД ДЛЯ ИНИЦИАЛИЗАЦИИ ПОЛЕЙ
    private void initializeUserFields(User user) {
        if (user.getExperiencePoints() == null) {
            user.setExperiencePoints(0);
        }
        if (user.getLevel() == null) {
            user.setLevel(1);
        }
    }

    public Map<String, Integer> getExperienceRewards() {
        return new HashMap<>(experienceRewards);
    }
}