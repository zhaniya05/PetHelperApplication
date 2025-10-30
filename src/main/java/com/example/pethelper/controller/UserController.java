package com.example.pethelper.controller;

import com.example.pethelper.dto.FollowDto;
import com.example.pethelper.dto.PetDto;
import com.example.pethelper.dto.PostDto;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.dto.UserActivityDto;
import com.example.pethelper.entity.User;
import com.example.pethelper.service.FollowService;
import com.example.pethelper.service.PostService;
import com.example.pethelper.service.UserActivityService;
import com.example.pethelper.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final FollowService followService;
    private final PostService postService;
    private  final UserActivityService userActivityService;

    public UserController(UserService userService,
                          FollowService followService,
                          PostService postService, UserActivityService userActivityService) {
        this.userService = userService;
        this.followService = followService;
        this.postService = postService;
        this.userActivityService = userActivityService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }



    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute UserDto updatedUserDto,
                             @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
                             RedirectAttributes redirectAttributes) {
        try {
            UserDto updatedUser = userService.updateUser(id, updatedUserDto, avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/api/users/viewProfile/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            return "redirect:/api/users/viewProfile/" + id;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/viewProfile/{id}")
    public String view_profile(@PathVariable Long id, Model model) {
        UserDto user = userService.getUserById(id);
        model.addAttribute("user", user);

        // ✅ ДОБАВЬТЕ ЗАГРУЗКУ ИСТОРИИ ДЕЙСТВИЙ
        try {
            List<UserActivityDto> recentActivities = userActivityService.getUserActivityHistory(id);
            // Берем только последние 5 действий
            List<UserActivityDto> limitedActivities = recentActivities.stream()
                    .limit(5)
                    .collect(Collectors.toList());
            model.addAttribute("recentActivities", limitedActivities);
        } catch (Exception e) {
            // Если возникла ошибка, устанавливаем пустой список
            model.addAttribute("recentActivities", new ArrayList<UserActivityDto>());
            System.out.println("Error loading activity history: " + e.getMessage());
        }

        return "profile";
    }


    @GetMapping("/viewUserProfile/{id}")
    public String viewUserProfile(@PathVariable Long id, Authentication authentication, Model model) {
        UserDto viewedUser = userService.getUserById(id);
        UserDto currentUser = userService.findByEmail(authentication.getName());

        boolean isOwner = viewedUser.getUserId().equals(currentUser.getUserId());
        boolean isFollowing = followService.isFollowing(currentUser, viewedUser);

        // Followers and following lists
        List<FollowDto> followers = followService.getFollowers(viewedUser);
        List<FollowDto> following = followService.getFollowing(viewedUser);

        // Posts — filter private ones if not following
        List<PostDto> posts = postService.getVisiblePosts(currentUser, viewedUser);

        model.addAttribute("user", currentUser);
        model.addAttribute("userProfile", viewedUser);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("followersCount", followers.size());
        model.addAttribute("followingCount", following.size());
        model.addAttribute("followers", followers);
        model.addAttribute("following", following);
        model.addAttribute("posts", posts);

        return "user-profile";
    }

    @GetMapping("/main")
    public String getUsers(@RequestParam(required = false) String keyword,
                           Model model,
                           Authentication authentication) {
        String email = authentication.getName();
        UserDto user = userService.findByEmail(email);

        List<UserDto> users = userService.getAllUsers();

        if (keyword != null && !keyword.trim().isEmpty()) {
            users = userService.searchUsersByKeyword(keyword);
        } else {
            users = userService.getAllUsers();
        }

        model.addAttribute("listUsers", users);
        model.addAttribute("user", user);
        model.addAttribute("keyword", keyword);



        return "users-list";
    }
}

