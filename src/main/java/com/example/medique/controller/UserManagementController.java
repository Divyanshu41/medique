package com.example.medique.controller;

import com.example.medique.model.User;
import com.example.medique.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class UserManagementController {
    private static final Set<String> ROLES = Set.of("ADMIN","DOCTOR","PATIENT");
    private final UserRepository users;
    public UserManagementController(UserRepository users) { this.users = users; }

    @GetMapping("/users")
    public String listAllUsers(Model model) {
        List<User> all = users.findAll(); model.addAttribute("users", all); return "admin_users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = users.findById(id).orElse(null);
        if (user == null) return "redirect:/admin/users";
        model.addAttribute("user", user); return "edit_user";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id, @RequestParam String name, @RequestParam String email,
            @RequestParam String role, @RequestParam(required=false) String phone,
            @RequestParam(required=false) Integer age, @RequestParam(required=false) String gender,
            @RequestParam(required=false) String address, @RequestParam(defaultValue="true") boolean isActive,
            RedirectAttributes redirect) {
        User user = users.findById(id).orElse(null);
        if (user == null || !ROLES.contains(role)) {
            redirect.addFlashAttribute("errorMessage", "User not found or invalid role.");
            return "redirect:/admin/users";
        }
        if (users.findByEmail(email).filter(existing -> !existing.getId().equals(id)).isPresent()) {
            redirect.addFlashAttribute("errorMessage", "Email is already in use.");
            return "redirect:/admin/users/edit/" + id;
        }
        user.setName(name.trim()); user.setEmail(email.trim().toLowerCase()); user.setRole(role);
        user.setPhone(phone); user.setAge(age); user.setGender(gender); user.setAddress(address);
        user.setIsActive(isActive); users.save(user);
        redirect.addFlashAttribute("successMessage", "User updated successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/change-role/{id}")
    @ResponseBody
    public String changeUserRole(@PathVariable Long id, @RequestParam String role) {
        if (!ROLES.contains(role)) return "Invalid role";
        User user = users.findById(id).orElse(null);
        if (user == null) return "User not found";
        user.setRole(role); users.save(user); return "success";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirect) {
        User user = users.findById(id).orElse(null);
        if (user == null) redirect.addFlashAttribute("errorMessage", "User not found.");
        else if ("ADMIN".equals(user.getRole()))
            redirect.addFlashAttribute("errorMessage", "Admin accounts cannot be deleted here.");
        else {
            user.setIsActive(false); users.save(user);
            redirect.addFlashAttribute("successMessage", "User deactivated; historical records preserved.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirect) {
        User user = users.findById(id).orElse(null);
        if (user == null) redirect.addFlashAttribute("errorMessage", "User not found.");
        else {
            user.setIsActive(!Boolean.TRUE.equals(user.getIsActive())); users.save(user);
            redirect.addFlashAttribute("successMessage", "User status updated.");
        }
        return "redirect:/admin/users";
    }
}
