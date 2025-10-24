package com.example.medique.controller;

import com.example.medique.model.User;
import com.example.medique.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Root URL - redirect to dashboard or login
     */
    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        System.out.println("\n");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("🔍 DASHBOARD ACCESS ATTEMPT");
        System.out.println("═══════════════════════════════════════════════════");

        if (principal == null) {
            System.out.println("❌ ERROR: No principal found");
            System.out.println("   → Redirecting to login");
            System.out.println("═══════════════════════════════════════════════════\n");
            return "redirect:/login";
        }

        System.out.println("✅ User authenticated: " + principal.getUsername());

        User user = userRepository.findByEmail(principal.getUsername()).orElse(null);
        if (user == null) {
            System.out.println("❌ ERROR: User not found in database");
            System.out.println("   Email searched: " + principal.getUsername());
            System.out.println("   → Redirecting to login");
            System.out.println("═══════════════════════════════════════════════════\n");
            return "redirect:/login";
        }

        System.out.println("✅ User found in database");
        System.out.println("   Name: " + user.getName());
        System.out.println("   Email: " + user.getEmail());
        System.out.println("   Role: " + user.getRole());

        // Route users to their appropriate dashboards based on role
        if ("ADMIN".equals(user.getRole())) {
            System.out.println("🔄 Redirecting to admin dashboard");
            System.out.println("═══════════════════════════════════════════════════\n");
            return "redirect:/admin/dashboard";
        } else if ("DOCTOR".equals(user.getRole())) {
            System.out.println("🔄 Showing doctor dashboard");
            System.out.println("═══════════════════════════════════════════════════\n");
            model.addAttribute("user", user);
            return "doctor_dashboard";
        } else if ("PATIENT".equals(user.getRole()) || "USER".equals(user.getRole())) {
            System.out.println("🔄 Showing patient/user dashboard");
            System.out.println("═══════════════════════════════════════════════════\n");
            model.addAttribute("name", user.getName());
            model.addAttribute("user", user);
            model.addAttribute("doctors", java.util.Collections.emptyList()); // Add empty doctors list for now
            return "user_dashboard";
        } else {
            System.out.println("⚠️ Unknown role: " + user.getRole());
            System.out.println("   → Showing default user dashboard");
            System.out.println("═══════════════════════════════════════════════════\n");
            model.addAttribute("name", user.getName());
            model.addAttribute("user", user);
            model.addAttribute("doctors", java.util.Collections.emptyList()); // Add empty doctors list for now
            return "user_dashboard";
        }
    }
}
