package com.example.medique.controller;

import com.example.medique.model.User;
import com.example.medique.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class UserManagementController {
    private static final Set<String> ROLES = Set.of("ADMIN", "DOCTOR", "PATIENT");
    private final UserRepository users;

    public UserManagementController(UserRepository users) { this.users = users; }

    @GetMapping("/users")
    public String listAllUsers(Model model) {
        List<User> all = users.findAll();
        model.addAttribute("users", all);
        return "admin_users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = users.findById(id).orElse(null);
        if (user == null) return "redirect:/admin/users";
        model.addAttribute("user", user);
        return "edit_user";
    }

    @PostMapping("/users/edit/{id}")
    @Transactional
    public String updateUser(@PathVariable Long id, @RequestParam String name, @RequestParam String email,
            @RequestParam String role, @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer age, @RequestParam(required = false) String gender,
            @RequestParam(required = false) String address, @RequestParam(defaultValue = "true") boolean isActive,
            @AuthenticationPrincipal UserDetails principal, RedirectAttributes redirect) {
        List<User> activeAdmins = users.findActiveAdminsForUpdate();
        User user = users.findById(id).orElse(null);
        if (user == null || !ROLES.contains(role)) {
            redirect.addFlashAttribute("errorMessage", "User not found or invalid role.");
            return "redirect:/admin/users";
        }
        if (!protectAdminAccess(user, role, isActive, principal, activeAdmins, redirect))
            return "redirect:/admin/users/edit/" + id;
        String normalizedEmail = email.trim().toLowerCase();
        if (users.findByEmail(normalizedEmail).filter(existing -> !existing.getId().equals(id)).isPresent()) {
            redirect.addFlashAttribute("errorMessage", "Email is already in use.");
            return "redirect:/admin/users/edit/" + id;
        }
        if (name == null || name.trim().length() < 2 || normalizedEmail.isBlank()) {
            redirect.addFlashAttribute("errorMessage", "Name and email are required.");
            return "redirect:/admin/users/edit/" + id;
        }
        user.setName(name.trim());
        user.setEmail(normalizedEmail);
        user.setRole(role);
        user.setPhone(phone);
        user.setAge(age);
        user.setGender(gender);
        user.setAddress(address);
        user.setIsActive(isActive);
        users.save(user);
        redirect.addFlashAttribute("successMessage", "User updated successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/change-role/{id}")
    @ResponseBody
    @Transactional
    public String changeUserRole(@PathVariable Long id, @RequestParam String role,
            @AuthenticationPrincipal UserDetails principal) {
        if (!ROLES.contains(role)) return "Invalid role";
        List<User> activeAdmins = users.findActiveAdminsForUpdate();
        User user = users.findById(id).orElse(null);
        if (user == null) return "User not found";
        if (wouldRemoveAdminAccess(user, role, Boolean.TRUE.equals(user.getIsActive()), principal, activeAdmins))
            return "Cannot remove access from the current or last active admin account";
        user.setRole(role);
        users.save(user);
        return "success";
    }

    @PostMapping("/users/delete/{id}")
    @Transactional
    public String deleteUser(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal,
            RedirectAttributes redirect) {
        List<User> activeAdmins = users.findActiveAdminsForUpdate();
        User user = users.findById(id).orElse(null);
        if (user == null) redirect.addFlashAttribute("errorMessage", "User not found.");
        else if ("ADMIN".equals(user.getRole())) {
            redirect.addFlashAttribute("errorMessage", "Admin accounts cannot be deactivated through this action.");
        } else {
            user.setIsActive(false);
            users.save(user);
            redirect.addFlashAttribute("successMessage", "User deactivated; historical records preserved.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/toggle-status/{id}")
    @Transactional
    public String toggleUserStatus(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal,
            RedirectAttributes redirect) {
        List<User> activeAdmins = users.findActiveAdminsForUpdate();
        User user = users.findById(id).orElse(null);
        if (user == null) {
            redirect.addFlashAttribute("errorMessage", "User not found.");
        } else {
            boolean nextActive = !Boolean.TRUE.equals(user.getIsActive());
            if (wouldRemoveAdminAccess(user, user.getRole(), nextActive, principal, activeAdmins)) {
                redirect.addFlashAttribute("errorMessage", "Cannot deactivate the current or last active admin account.");
            } else {
                user.setIsActive(nextActive);
                users.save(user);
                redirect.addFlashAttribute("successMessage", "User status updated.");
            }
        }
        return "redirect:/admin/users";
    }

    private boolean protectAdminAccess(User target, String nextRole, boolean nextActive, UserDetails principal,
            List<User> activeAdmins, RedirectAttributes redirect) {
        if (!wouldRemoveAdminAccess(target, nextRole, nextActive, principal, activeAdmins)) return true;
        redirect.addFlashAttribute("errorMessage", "Cannot remove access from the current or last active admin account.");
        return false;
    }

    private boolean wouldRemoveAdminAccess(User target, String nextRole, boolean nextActive, UserDetails principal,
            List<User> activeAdmins) {
        boolean removingAdminRole = "ADMIN".equals(target.getRole()) && (!"ADMIN".equals(nextRole) || !nextActive);
        if (!removingAdminRole) return false;
        if (principal != null && target.getEmail().equalsIgnoreCase(principal.getUsername())) return true;
        return activeAdmins.size() <= 1;
    }
}
