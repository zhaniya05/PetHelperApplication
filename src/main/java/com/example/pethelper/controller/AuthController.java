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
        model.addAttribute("userDto", new UserDto());
        return "register";
    }


    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }


//    @PostMapping("/register")
//    public String register(@ModelAttribute("user") UserDto userDto, Model model) {
//        try {
//            UserDto savedUser = userService.register(userDto);
//            model.addAttribute("successMessage", "Registration successful! Please log in.");
//            return "login";
//        } catch (RuntimeException e) {
//           // model.addAttribute("errorMessage", e.getMessage());
//            model.addAttribute("errorMessage", "Error: " + e.getMessage());
//            return "register";
//        }
//    }

    @PostMapping("/register")
    public String register(@ModelAttribute("userDto") UserDto userDto, Model model) {
        System.out.println("Received userDto: " + userDto);
        System.out.println("Is userDto null? " + (userDto == null));

        if (userDto == null) {
            model.addAttribute("errorMessage", "User data is null - form binding failed");
            return "register";
        }

        try {
            userService.register(userDto);
            return "redirect:/auth/login";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            model.addAttribute("userDto", userDto);
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
