package com.example.myturn.controller;

import com.example.myturn.model.User;
import com.example.myturn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserManagementController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Display all users
     */
    @GetMapping("/users")
    public String listAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin_users";
    }

    /**
     * Show edit user form
     */
    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "edit_user";
    }

    /**
     * Update user role and details
     */
    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id,
                            @RequestParam String name,
                            @RequestParam String email,
                            @RequestParam String role,
                            @RequestParam(required = false) String phone,
                            @RequestParam(required = false) Integer age,
                            @RequestParam(required = false) String gender,
                            @RequestParam(required = false) String address,
                            @RequestParam(defaultValue = "true") boolean isActive,
                            RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setName(name);
            user.setEmail(email);
            user.setRole(role);
            user.setPhone(phone);
            user.setAge(age);
            user.setGender(gender);
            user.setAddress(address);
            user.setIsActive(isActive);
            
            userRepository.save(user);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "User updated successfully! Role changed to: " + role);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error updating user: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    /**
     * Quick role change (AJAX endpoint)
     */
    @PostMapping("/users/change-role/{id}")
    @ResponseBody
    public String changeUserRole(@PathVariable Long id, @RequestParam String role) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setRole(role);
            userRepository.save(user);
            
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    /**
     * Delete user
     */
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * Toggle user active status
     */
    @GetMapping("/users/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setIsActive(!user.getIsActive());
            userRepository.save(user);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "User status updated to: " + (user.getIsActive() ? "Active" : "Inactive"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error updating status: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
