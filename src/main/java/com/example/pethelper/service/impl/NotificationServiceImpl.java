package com.example.pethelper.service.impl;

import com.example.pethelper.entity.Notification;
import com.example.pethelper.entity.User;
import com.example.pethelper.repository.NotificationRepository;
import com.example.pethelper.repository.UserRepository;
import com.example.pethelper.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public void createNotification(Long recipientId, String message, String link) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setMessage(message);
        notification.setLink(link);
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUserNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

//    @Override
//    public List<Notification> getNotificationsByUser(User user) {
//        return notificationRepository.findByUser(user);
//    }
}
