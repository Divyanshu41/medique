package com.example.medique.controller;

import com.example.medique.model.User;
import com.example.medique.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result) {
        if (users.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "duplicate", "This email is already registered");
        }
        if (result.hasErrors()) return "register";
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("PATIENT");
        user.setIsActive(true);
        users.save(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String logout, Model model) {
        if (error != null) model.addAttribute("loginError", "Invalid email or password.");
        if (logout != null) model.addAttribute("logoutMessage", "You have been logged out.");
        return "login";
    }
}
