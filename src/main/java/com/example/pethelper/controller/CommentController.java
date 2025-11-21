package com.example.pethelper.controller;

import com.example.pethelper.dto.CommentDto;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.service.CommentService;
import com.example.pethelper.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Controller
@AllArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @GetMapping
    public String getComments(@PathVariable Long postId,
                              Model model,
                              Authentication authentication) {

        Long currentUserId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            try {
                UserDto currentUser = userService.findByEmail(authentication.getName());
                model.addAttribute("user", currentUser);
                model.addAttribute("currentUserId", currentUser.getUserId());
                currentUserId = currentUser.getUserId();
            } catch (Exception e) {
                model.addAttribute("user", null);
                model.addAttribute("currentUserId", null);
            }
        } else {
            model.addAttribute("user", null);
            model.addAttribute("currentUserId", null);
        }

        // Pass currentUserId to service to check likes
        List<CommentDto> comments = commentService.getCommentsByPost(postId, currentUserId);
        model.addAttribute("comments", comments);
        model.addAttribute("postId", postId);

        return "comments";
    }

    @PostMapping
    public String addComment(@PathVariable Long postId,
                             @RequestParam String commentContent,
                             Authentication authentication,
                             Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        CommentDto commentDTO = new CommentDto();
        commentDTO.setCommentContent(commentContent);

        // Get current user ID
        Long currentUserId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                UserDto currentUser = userService.findByEmail(authentication.getName());
                currentUserId = currentUser.getUserId();
            } catch (Exception e) {
                System.out.println("error");
            }
        }

        commentService.addComment(postId, commentDTO, authentication.getName(), currentUserId);

        return "redirect:/posts/" + postId + "/comments";
    }

    @GetMapping("/{commentId}")
    public String deleteComment(@PathVariable Long commentId,
                                @PathVariable Long postId,
                                Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        commentService.deleteComment(commentId, authentication.getName());
        return "redirect:/posts/" + postId + "/comments";
    }

    @PostMapping("/{commentId}/like")
    public String likeComment(@PathVariable Long postId,
                              @PathVariable Long commentId,
                              Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            commentService.likeComment(commentId, authentication.getName());
        } catch (RuntimeException e) {
            commentService.unlikeComment(commentId, authentication.getName());
        }

        return "redirect:/posts/" + postId + "/comments";
    }

    // Add unlike endpoint
    @PostMapping("/{commentId}/unlike")
    public String unlikeComment(@PathVariable Long postId,
                                @PathVariable Long commentId,
                                Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            commentService.unlikeComment(commentId, authentication.getName());
        } catch (RuntimeException e) {
            commentService.likeComment(commentId, authentication.getName());
        }

        return "redirect:/posts/" + postId + "/comments";
    }

}