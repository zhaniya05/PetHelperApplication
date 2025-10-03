package com.example.pethelper.controller;

import com.example.pethelper.dto.LoginRequest;
import com.example.pethelper.dto.UserDto;
import com.example.pethelper.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "register";
    }


    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }


    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserDto userDto, Model model) {
        try {
            UserDto savedUser = userService.register(userDto);
            model.addAttribute("successMessage", "Registration successful! Please log in.");
            return "login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }


    @PostMapping("/login")
    public String login(@ModelAttribute("loginRequest") LoginRequest loginRequest,
                        HttpSession session,
                        Model model) {
        try {
            UserDto userDto = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            session.setAttribute("userId", userDto.getUserId());
            return "redirect:/main";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Invalid username or password");
            return "login";
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out");
    }
}
