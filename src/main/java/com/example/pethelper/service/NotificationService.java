package com.example.pethelper.service;

import com.example.pethelper.entity.Notification;
import com.example.pethelper.entity.User;

import java.util.List;

public interface NotificationService {
    void createNotification(Long recipientId, String message, String link);
    List<Notification> getUserNotifications(Long userId);
    void markAsRead(Long notificationId);
   // List<Notification> getNotificationsByUser(User user);
}
