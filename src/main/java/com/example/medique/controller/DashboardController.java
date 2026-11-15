package com.example.medique.controller;

import com.example.medique.model.User;
import com.example.medique.model.view.DoctorView;
import com.example.medique.model.view.HospitalView;
import com.example.medique.repository.AppointmentRepository;
import com.example.medique.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
    private final UserRepository users;
    private final AppointmentRepository appointments;
    public DashboardController(UserRepository users, AppointmentRepository appointments) {
        this.users = users;
        this.appointments = appointments;
    }
    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails principal) {
        return principal == null ? "redirect:/login" : "redirect:/dashboard";
    }
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        if (principal == null) return "redirect:/login";
        User user = users.findByEmail(principal.getUsername()).orElse(null);
        if (user == null || !Boolean.TRUE.equals(user.getIsActive())) return "redirect:/login";
        switch (user.getRole()) {
            case "ADMIN": return "redirect:/admin/dashboard";
            case "DOCTOR":
                return "redirect:/doctor/appointments";
            case "PATIENT":
            case "USER":
                model.addAttribute("name", user.getName());
                model.addAttribute("user", user);
                model.addAttribute("doctors", activeDoctorCards());
                return "user_dashboard";
            default: return "redirect:/login?error";
        }
    }
    private List<DoctorView> activeDoctorCards() {
        List<DoctorView> result = new ArrayList<>();
        for (User doctor : users.findByRole("DOCTOR")) {
            if (!Boolean.TRUE.equals(doctor.getIsActive())) continue;
            DoctorView view = new DoctorView();
            view.setId(doctor.getId());
            view.setName(doctor.getName());
            view.setEmail(doctor.getEmail());
            view.setPhone(doctor.getPhone());
            view.setSpecialization(doctor.getAddress());
            view.setHospital(new HospitalView("Clinic"));
            view.setWorkingHours("09:00-17:00");
            result.add(view);
            if (result.size() == 6) break;
        }
        return result;
    }
}
