package com.example.pethelper.service.impl;


import com.example.pethelper.entity.ActivityType;
import com.example.pethelper.entity.User;
import com.example.pethelper.entity.UserActivity;
import com.example.pethelper.dto.UserActivityDto;
import com.example.pethelper.exception.ResourceNotFoundException;
import com.example.pethelper.repository.UserActivityRepository;
import com.example.pethelper.repository.UserRepository;
import com.example.pethelper.service.UserActivityService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserActivityServiceImpl implements UserActivityService {

    private final UserActivityRepository userActivityRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void logActivity(User user, ActivityType activityType, String description,
                            String targetEntity, Long targetId) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType(activityType);
        activity.setDescription(description);
        activity.setTargetEntity(targetEntity);
        activity.setTargetId(targetId);

        userActivityRepository.save(activity);
    }

    @Override
    public List<UserActivityDto> getUserActivityHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userActivityRepository.findByUserOrderByActivityDateDesc(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserActivityDto> getUserActivityHistory(Long userId, String entityType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userActivityRepository.findByUserAndTargetEntityOrderByActivityDateDesc(user, entityType)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private UserActivityDto toDto(UserActivity activity) {
        UserActivityDto dto = new UserActivityDto();
        dto.setId(activity.getId());
        dto.setActivityType(activity.getActivityType().name());
        dto.setDescription(activity.getDescription());
        dto.setTargetEntity(activity.getTargetEntity());
        dto.setTargetId(activity.getTargetId());
        dto.setActivityDate(activity.getActivityDate());
        dto.setFormattedDate(activity.getActivityDate().format(
                DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm")));
        return dto;
    }
}