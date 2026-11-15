package com.example.medique.controller;

import com.example.medique.model.User;
import com.example.medique.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

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
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
                               Model model) {
        // Public self-registration is restricted to patient accounts.
        user.setRole("PATIENT");
        user.setIsActive(true);

        String normalizedEmail = user.getEmail() == null
                ? ""
                : user.getEmail().trim().toLowerCase(Locale.ROOT);

        if (!result.hasFieldErrors("email") && users.existsByEmailIgnoreCase(normalizedEmail)) {
            result.rejectValue("email", "duplicate", "This email is already registered.");
        }

        if (result.hasErrors()) {
            // Never echo a submitted password back into the registration form.
            user.setPassword(null);
            return "register";
        }

        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        users.save(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String logout,
                                @RequestParam(required = false) String registered,
                                Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Invalid email or password, or your account is inactive.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out.");
        }
        if (registered != null) {
            model.addAttribute("registeredMessage", "Account created successfully. You can now sign in.");
        }
        return "login";
    }
}
