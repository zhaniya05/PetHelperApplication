package com.example.pethelper.controller;

import com.example.pethelper.dto.FollowDto;
import com.example.pethelper.dto.PetDto;
import com.example.pethelper.dto.PostDto;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.User;
import com.example.pethelper.service.FollowService;
import com.example.pethelper.service.PostService;
import com.example.pethelper.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final FollowService followService;
    private final PostService postService;

    public UserController(UserService userService,
                          FollowService followService,
                          PostService postService) {
        this.userService = userService;
        this.followService = followService;
        this.postService = postService;
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
}

