package com.example.medique.controller;

import com.example.medique.model.Appointment;
import com.example.medique.model.User;
import com.example.medique.model.view.DoctorView;
import com.example.medique.model.view.HospitalView;
import com.example.medique.repository.AppointmentRepository;
import com.example.medique.repository.UserRepository;
import com.example.medique.service.AppointmentService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserRepository users;
    private final AppointmentRepository appointments;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentService appointmentService;

    public AdminController(UserRepository users, AppointmentRepository appointments,
            PasswordEncoder passwordEncoder, AppointmentService appointmentService) {
        this.users = users;
        this.appointments = appointments;
        this.passwordEncoder = passwordEncoder;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("doctorCount", users.countByRole("DOCTOR"));
        model.addAttribute("patientCount", users.countByRole("PATIENT"));
        model.addAttribute("appointmentCount", appointments.count());
        List<DoctorView> recentDoctors = new ArrayList<>();
        for (User user : users.findByRole("DOCTOR")) {
            DoctorView view = new DoctorView();
            view.setId(user.getId()); view.setName(user.getName()); view.setEmail(user.getEmail());
            view.setPhone(user.getPhone()); view.setSpecialization(user.getAddress());
            view.setHospital(new HospitalView("Clinic")); view.setWorkingHours("09:00-17:00");
            recentDoctors.add(view);
            if (recentDoctors.size() == 5) break;
        }
        model.addAttribute("recentDoctors", recentDoctors);
        return "admin_dashboard";
    }

    @PostMapping("/add-doctor")
    public String addDoctor(@RequestParam String name, @RequestParam String specialization,
            @RequestParam String email, @RequestParam String phone, @RequestParam String password,
            RedirectAttributes redirect) {
        if (name == null || name.trim().length() < 2 || specialization == null || specialization.isBlank()
                || email == null || email.isBlank() || password == null || password.length() < 8) {
            redirect.addFlashAttribute("error", "Enter valid doctor details. Password must be at least 8 characters.");
            return "redirect:/admin/add-doctor";
        }
        String normalizedEmail = email.trim().toLowerCase();
        if (users.existsByEmail(normalizedEmail)) {
            redirect.addFlashAttribute("error", "An account with this email already exists.");
            return "redirect:/admin/add-doctor";
        }
        User doctor = new User();
        doctor.setName(name.trim()); doctor.setEmail(normalizedEmail); doctor.setPhone(phone);
        doctor.setRole("DOCTOR"); doctor.setPassword(passwordEncoder.encode(password));
        doctor.setAddress(specialization.trim()); doctor.setIsActive(true);
        users.save(doctor);
        redirect.addFlashAttribute("success", "Doctor added successfully.");
        return "redirect:/admin/doctors";
    }

    @GetMapping("/doctors")
    public String listDoctors(Model model) {
        List<DoctorView> doctors = new ArrayList<>();
        for (User user : users.findByRole("DOCTOR")) {
            DoctorView view = new DoctorView();
            view.setId(user.getId()); view.setName(user.getName()); view.setEmail(user.getEmail());
            view.setPhone(user.getPhone()); view.setSpecialization(user.getAddress());
            view.setHospital(new HospitalView("Clinic")); view.setWorkingHours("09:00-17:00");
            doctors.add(view);
        }
        model.addAttribute("doctors", doctors);
        return "admin_doctors";
    }

    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        model.addAttribute("appointments", appointments.findAll());
        return "admin_appointments";
    }

    @PostMapping("/appointments/update-status/{id}")
    public String updateAppointmentStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes redirect) {
        try {
            appointmentService.transitionStatus(id, status);
            redirect.addFlashAttribute("success", "Appointment status updated.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/appointments";
    }

    @GetMapping("/add-doctor")
    public String showAddDoctorForm() { return "add_doctor"; }

    @GetMapping("/doctors/edit/{id}")
    public String showEditDoctorForm(@PathVariable Long id, Model model) {
        User doctor = users.findById(id).orElse(null);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) return "redirect:/admin/doctors";
        model.addAttribute("doctor", doctor);
        return "edit_doctor";
    }

    @PostMapping("/doctors/edit/{id}")
    public String updateDoctor(@PathVariable Long id, @RequestParam String name, @RequestParam String email,
            @RequestParam String phone, @RequestParam String specialization, RedirectAttributes redirect) {
        User doctor = users.findById(id).orElse(null);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) return "redirect:/admin/doctors";
        String normalizedEmail = email.trim().toLowerCase();
        if (users.findByEmail(normalizedEmail).filter(existing -> !existing.getId().equals(id)).isPresent()) {
            redirect.addFlashAttribute("error", "Email is already used by another account.");
            return "redirect:/admin/doctors/edit/" + id;
        }
        doctor.setName(name.trim()); doctor.setEmail(normalizedEmail); doctor.setPhone(phone);
        doctor.setAddress(specialization.trim()); users.save(doctor);
        redirect.addFlashAttribute("success", "Doctor updated successfully.");
        return "redirect:/admin/doctors";
    }

    @PostMapping("/doctors/delete/{id}")
    public String deleteDoctor(@PathVariable Long id, RedirectAttributes redirect) {
        User doctor = users.findById(id).orElse(null);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            redirect.addFlashAttribute("error", "Doctor not found.");
        } else if (!appointments.findByDoctor(doctor).isEmpty()) {
            doctor.setIsActive(false); users.save(doctor);
            redirect.addFlashAttribute("error", "Doctor has appointment history; account deactivated instead.");
        } else {
            users.delete(doctor);
            redirect.addFlashAttribute("success", "Doctor deleted.");
        }
        return "redirect:/admin/doctors";
    }
}
