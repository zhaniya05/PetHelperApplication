package com.example.pethelper.controller;

import com.example.pethelper.dto.FollowDto;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.User;
import com.example.pethelper.service.FollowService;
import com.example.pethelper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    // Send a follow request
    @PostMapping("/request/{userId}")
    public String sendFollowRequest(@PathVariable Long userId, Authentication authentication) {
        UserDto follower = userService.findByEmail(authentication.getName());
        UserDto following = userService.getUserById(userId);
        followService.sendFollowRequest(follower, following);
        return "redirect:/users/" + userId; // redirect to viewed user's profile
    }

    // View all pending follow requests (for the logged-in user)
    @GetMapping("/requests")
    public String viewRequests(Authentication authentication, Model model) {
        UserDto currentUser = userService.findByEmail(authentication.getName());
        List<FollowDto> requests = followService.getPendingRequests(currentUser);
        model.addAttribute("requests", requests);
        return "followRequests";
    }

    // Accept follow request
    @PostMapping("/accept/{id}")
    public String acceptFollow(@PathVariable Long id) {
        followService.acceptFollowRequest(id);
        return "redirect:/follow/requests";
    }

    // Reject follow request
    @PostMapping("/reject/{id}")
    public String rejectFollow(@PathVariable Long id) {
        followService.rejectFollowRequest(id);
        return "redirect:/follow/requests";
    }

    // View followers of current user
    @GetMapping("/followers")
    public String viewFollowers(Authentication authentication, Model model) {
        UserDto currentUser = userService.findByEmail(authentication.getName());
        List<FollowDto> followers = followService.getFollowers(currentUser);
        model.addAttribute("followers", followers);
        return "followers";
    }

    // View users current user follows
    @GetMapping("/following")
    public String viewFollowing(Authentication authentication, Model model) {
        UserDto currentUser = userService.findByEmail(authentication.getName());
        List<FollowDto> following = followService.getFollowing(currentUser);
        model.addAttribute("following", following);
        return "following";
    }
}


