package com.example.pethelper.controller;


import com.example.pethelper.entity.Poll;
import com.example.pethelper.service.UserService;
import com.example.pethelper.service.impl.PollService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/polls")
public class PollController {

    private final PollService pollService;
    private final UserService userService;

    @PostMapping("/{pollId}/vote")
    public String vote(
            @PathVariable Long pollId,
            @RequestParam Long optionId,
            Authentication auth, Model model) {

        Long userId = userService.findByEmail(auth.getName()).getUserId();
        pollService.vote(pollId, optionId, userId);


        return "redirect:/posts";
    }

    @GetMapping("/{pollId}")
    public Poll getPoll(@PathVariable Long pollId) {
        return pollService.getPollById(pollId);
    }
}
