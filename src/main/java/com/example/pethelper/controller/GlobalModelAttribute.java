package com.example.pethelper.controller;

import com.example.pethelper.dto.FollowDto;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.Notification;
import com.example.pethelper.entity.User;
import com.example.pethelper.service.FollowService;
//import com.example.pethelper.service.NotificationService;
import com.example.pethelper.service.NotificationService;
import com.example.pethelper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.util.List;

//@ControllerAdvice
//public class GlobalModelAttribute {
//
//    @Autowired
//    private FollowService followService;
//
//    @Autowired
//    private NotificationService notificationService;
//
//    @Autowired
//    private UserService userService;
//
//    @ModelAttribute
//    public void addGlobalAttributes(Model model) {
//        User currentUser = userService.getCurrentUser();
//
//        if (currentUser != null) {
//            Long userId = currentUser.getUserId();
//            model.addAttribute("requests", followService.getPendingRequests(userService.getUserById(userId)));
//            //model.addAttribute("postNotifications", notificationService.getNotificationsByUser(currentUser));
//        } else {
//            model.addAttribute("requests", List.of());
//            model.addAttribute("postNotifications", List.of());
//        }
//    }
//}

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttribute {

    private final FollowService followService;
    private final NotificationService notificationService;
    private final UserService userService;

    @ModelAttribute
    public void addGlobalAttributes(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDto currentUser = userService.findByEmail(authentication.getName());

            // pending follow requests (DTO)
            List<FollowDto> requests = followService.getPendingRequests(currentUser);

            // post notifications (entities)
            List<Notification> postNotifications =
                    notificationService.getUserNotifications(currentUser.getUserId());

            model.addAttribute("user", currentUser);
            model.addAttribute("requests", requests);
            model.addAttribute("postNotifications", postNotifications);
        } else {
            model.addAttribute("user", null);
            model.addAttribute("requests", List.of());
            model.addAttribute("postNotifications", List.of());
        }
    }
}

