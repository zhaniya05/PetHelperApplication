package com.example.pethelper.controller;

import com.example.pethelper.dto.FollowDto;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.Notification;
import com.example.pethelper.service.FollowService;
import com.example.pethelper.service.NotificationService;
import com.example.pethelper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

//    private final NotificationService notificationService;
//    private final UserService userService;
//
//    @GetMapping
//    public String viewNotifications(Model model, Authentication authentication) {
//        UserDto currentUser = userService.findByEmail(authentication.getName());
//        List<Notification> notifications = notificationService.getUserNotifications(currentUser.getUserId());
//        model.addAttribute("notifications", notifications);
//        model.addAttribute("user", currentUser);
//        return "notifications";
//    }
//
//    @PostMapping("/{id}/read")
//    public String markAsRead(@PathVariable Long id) {
//        notificationService.markAsRead(id);
//        return "redirect:/notifications";
//    }

    private final FollowService followService;
    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public String viewNotifications(Authentication authentication, Model model) {
        UserDto currentUser = userService.findByEmail(authentication.getName());

        List<FollowDto> requests = followService.getPendingRequests(currentUser);

        List<Notification> postNotifications = notificationService.getUserNotifications(currentUser.getUserId());

        model.addAttribute("user", currentUser);
        model.addAttribute("requests", requests);
        model.addAttribute("postNotifications", postNotifications);

        return "notifications";
    }

    @PostMapping("/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    @PostMapping("/accept/{id}")
    public String acceptFollow(@PathVariable Long id) {
        followService.acceptFollowRequest(id);
        return "redirect:/notifications";
    }

    @PostMapping("/reject/{id}")
    public String rejectFollow(@PathVariable Long id) {
        followService.rejectFollowRequest(id);
        return "redirect:/notifications";
    }
}

