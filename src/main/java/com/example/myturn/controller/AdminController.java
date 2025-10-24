package com.example.myturn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.myturn.model.User;
import com.example.myturn.model.view.DoctorView;
import com.example.myturn.model.view.HospitalView;
import com.example.myturn.model.view.AppointmentView;
import com.example.myturn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Simple admin dashboard counts (placeholder for now)
        Long doctorCount = userRepository.countByRole("DOCTOR");
        model.addAttribute("doctorCount", doctorCount);
        model.addAttribute("appointmentCount", 0);
        return "admin_dashboard";
    }


    @PostMapping("/add-doctor")
    public String addDoctor(@RequestParam String name,
                            @RequestParam String specialization,
                            @RequestParam String email,
                            @RequestParam String phone,
                            Model model) {
        User doctor = new User();
        doctor.setName(name);
        doctor.setEmail(email);
        doctor.setPhone(phone);
        doctor.setRole("DOCTOR");
        doctor.setPassword("doctor123"); // Default password, should be changed later
        // You can add specialization to address or extend User model if needed
        doctor.setAddress(specialization);
        doctor.setIsActive(true);
        userRepository.save(doctor);
        return "redirect:/admin/doctors";
    }

    @GetMapping("/doctors")
    public String listDoctors(Model model) {
        var doctorUsers = userRepository.findByRole("DOCTOR");
        java.util.List<DoctorView> doctors = new java.util.ArrayList<>();
        for (User u : doctorUsers) {
            DoctorView d = new DoctorView();
            d.setId(u.getId());
            d.setName(u.getName());
            d.setEmail(u.getEmail());
            d.setPhone(u.getPhone());
            d.setSpecialization(u.getAddress()); // we stored specialization in address for now
            d.setHospital(new HospitalView("Clinic"));
            d.setWorkingHours("09:00-17:00");
            doctors.add(d);
        }
        model.addAttribute("doctors", doctors);
        return "admin_doctors";
    }

    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        // Placeholder: empty list for now
        java.util.List<AppointmentView> appointments = new java.util.ArrayList<>();
        model.addAttribute("appointments", appointments);
        return "admin_appointments";
    }

    @GetMapping("/add-doctor")
    public String showAddDoctorForm(Model model) {
        // TODO: Add any required model attributes
        return "add_doctor";
    }

    @GetMapping("/doctors/edit/{id}")
    public String showEditDoctorForm(@PathVariable Long id, Model model) {
        User doctor = userRepository.findById(id).orElse(null);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            return "redirect:/admin/doctors";
        }
        model.addAttribute("doctor", doctor);
        return "edit_doctor";
    }

    @PostMapping("/doctors/edit/{id}")
    public String updateDoctor(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String phone,
                               @RequestParam String specialization,
                               Model model) {
        User doctor = userRepository.findById(id).orElse(null);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            return "redirect:/admin/doctors";
        }
        doctor.setName(name);
        doctor.setEmail(email);
        doctor.setPhone(phone);
        doctor.setAddress(specialization); // storing specialization in address
        userRepository.save(doctor);
        return "redirect:/admin/doctors";
    }

    @GetMapping("/doctors/delete/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        User doctor = userRepository.findById(id).orElse(null);
        if (doctor != null && "DOCTOR".equals(doctor.getRole())) {
            userRepository.deleteById(id);
        }
        return "redirect:/admin/doctors";
    }
}
