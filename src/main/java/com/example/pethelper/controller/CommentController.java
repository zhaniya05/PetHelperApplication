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

        List<CommentDto> comments = commentService.getCommentsByPost(postId);
        model.addAttribute("comments", comments);
        model.addAttribute("postId", postId);

        if (authentication != null && authentication.isAuthenticated()) {
            try {
                UserDto currentUser = userService.findByEmail(authentication.getName());
                model.addAttribute("user", currentUser);
                model.addAttribute("currentUserId", currentUser.getUserId());
            } catch (Exception e) {

                model.addAttribute("user", null);
                model.addAttribute("currentUserId", null);
            }
        } else {

            model.addAttribute("user", null);
            model.addAttribute("currentUserId", null);
        }

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

        commentService.addComment(postId, commentDTO, authentication.getName());

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
}