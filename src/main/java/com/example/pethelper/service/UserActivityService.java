package com.example.pethelper.service;

import com.example.pethelper.dto.UserActivityDto;
import com.example.pethelper.entity.User;
import com.example.pethelper.entity.ActivityType;

import java.util.List;

public interface UserActivityService {
    void logActivity(User user, ActivityType activityType, String description,
                     String targetEntity, Long targetId);
   List<UserActivityDto> getUserActivityHistory(Long userId);
    List<UserActivityDto> getUserActivityHistory(Long userId, String entityType);
}