package com.example.pethelper.controller;

import com.example.pethelper.dto.UserDto;
import com.example.pethelper.entity.Tag;
import com.example.pethelper.entity.User;
import com.example.pethelper.service.FollowService;
import com.example.pethelper.service.TagService;
import com.example.pethelper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagFollowController {
    private final TagService tagFollowService;
    private final UserService userService;

    @PostMapping("/{name}/follow")
    public String followTag(@PathVariable String name, Authentication authentication) {
        UserDto user = userService.findByEmail(authentication.getName());
        tagFollowService.followTag(user, name);
        return "redirect:/tags/" + name;
    }

    @PostMapping("/{name}/unfollow")
    public String unfollowTag(@PathVariable String name, Authentication authentication) {
        UserDto user = userService.findByEmail(authentication.getName());
        tagFollowService.unfollowTag(user, name);
        return "redirect:/tags/" + name;
    }

    @GetMapping("/{name}")
    public String getTagPage(@PathVariable String name, Model model,
                             Authentication authentication) {
        Tag tag = tagFollowService.findByName(name)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        model.addAttribute("tag", tag);

        UserDto user = userService.findByEmail(authentication.getName());

        boolean isFollowing = tagFollowService.isUserFollowingTag(user, name);
        model.addAttribute("isFollowing", isFollowing);

        return "tagPage";
    }

}

