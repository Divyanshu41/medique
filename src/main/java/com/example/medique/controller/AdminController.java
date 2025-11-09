package com.example.medique.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.medique.model.Appointment;
import com.example.medique.model.User;
import com.example.medique.model.view.DoctorView;
import com.example.medique.model.view.HospitalView;
import com.example.medique.model.view.AppointmentView;
import com.example.medique.repository.AppointmentRepository;
import com.example.medique.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Get actual counts from database
        Long doctorCount = userRepository.countByRole("DOCTOR");
        Long patientCount = userRepository.countByRole("PATIENT");
        Long appointmentCount = appointmentRepository.count();
        
        model.addAttribute("doctorCount", doctorCount);
        model.addAttribute("patientCount", patientCount);
        model.addAttribute("appointmentCount", appointmentCount);
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
        System.out.println("🔍 ADMIN: Viewing all appointments");
        
        // Fetch all appointments from database
        java.util.List<Appointment> appointments = appointmentRepository.findAll();
        
        System.out.println("✅ Found " + appointments.size() + " appointments");
        
        model.addAttribute("appointments", appointments);
        return "admin_appointments";
    }

    @PostMapping("/appointments/update-status/{id}")
    public String updateAppointmentStatus(@PathVariable Long id, 
                                          @RequestParam String status) {
        System.out.println("🔄 ADMIN: Updating appointment " + id + " status to " + status);
        
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment != null) {
            appointment.setStatus(status);
            appointmentRepository.save(appointment);
            System.out.println("✅ Appointment status updated successfully");
        } else {
            System.out.println("❌ Appointment not found");
        }
        
        return "redirect:/admin/appointments";
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
