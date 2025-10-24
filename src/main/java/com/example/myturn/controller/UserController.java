package com.example.myturn.controller;

import com.example.myturn.model.Appointment;
import com.example.myturn.model.User;
import com.example.myturn.model.view.DoctorView;
import com.example.myturn.model.view.HospitalView;
import com.example.myturn.repository.AppointmentRepository;
import com.example.myturn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    /**
     * Show list of all doctors
     */
    @GetMapping("/doctors")
    public String viewDoctors(@AuthenticationPrincipal UserDetails principal, Model model) {
        System.out.println("🔍 USER: Viewing doctors list");
        
        User user = userRepository.findByEmail(principal.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        
        // Fetch all doctors from database
        var doctorUsers = userRepository.findByRole("DOCTOR");
        java.util.List<DoctorView> doctors = new java.util.ArrayList<>();
        for (User u : doctorUsers) {
            DoctorView d = new DoctorView();
            d.setId(u.getId());
            d.setName(u.getName());
            d.setEmail(u.getEmail());
            d.setPhone(u.getPhone());
            d.setSpecialization(u.getAddress()); // we stored specialization in address
            d.setHospital(new HospitalView("Clinic"));
            d.setWorkingHours("09:00-17:00");
            doctors.add(d);
        }
        model.addAttribute("doctors", doctors);
        
        return "user_doctors";
    }

    /**
     * Show user's appointments
     */
    @GetMapping("/appointments")
    public String viewAppointments(@AuthenticationPrincipal UserDetails principal, Model model) {
        System.out.println("🔍 USER: Viewing appointments");
        
        User user = userRepository.findByEmail(principal.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        
        // Fetch all appointments for this patient
        var appointments = appointmentRepository.findByPatient(user);
        model.addAttribute("appointments", appointments);
        
        return "my_appointments";
    }

    /**
     * Show booking form
     */
    @GetMapping("/book-appointment/{doctorId}")
    public String showBookingForm(@PathVariable Long doctorId, 
                                  @AuthenticationPrincipal UserDetails principal, 
                                  Model model) {
        System.out.println("🔍 USER: Booking appointment with doctor ID: " + doctorId);
        
        User user = userRepository.findByEmail(principal.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("doctorId", doctorId);
        
        return "book_appointment";
    }

    /**
     * Process appointment booking
     */
    @PostMapping("/book-appointment/{doctorId}")
    public String processBooking(@PathVariable Long doctorId,
                                @RequestParam("date") String date,
                                @RequestParam("slot") String slot,
                                @AuthenticationPrincipal UserDetails principal,
                                RedirectAttributes redirectAttributes) {
        System.out.println("📝 USER: Processing appointment booking");
        System.out.println("   Doctor ID: " + doctorId);
        System.out.println("   Date: " + date);
        System.out.println("   Slot: " + slot);
        
        try {
            // Get current user (patient)
            User patient = userRepository.findByEmail(principal.getUsername()).orElse(null);
            if (patient == null) {
                return "redirect:/login";
            }
            
            // Get doctor
            User doctor = userRepository.findById(doctorId).orElse(null);
            if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
                redirectAttributes.addFlashAttribute("error", "Doctor not found");
                return "redirect:/user/doctors";
            }
            
            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setPatient(patient);
            appointment.setDoctor(doctor);
            appointment.setAppointmentDate(LocalDate.parse(date));
            appointment.setTimeSlot(slot);
            appointment.setStatus("CONFIRMED");
            
            // Save appointment
            appointmentRepository.save(appointment);
            
            System.out.println("✅ Appointment created successfully!");
            redirectAttributes.addFlashAttribute("success", "Appointment booked successfully!");
            
            return "redirect:/user/appointments";
            
        } catch (Exception e) {
            System.out.println("❌ Error booking appointment: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to book appointment: " + e.getMessage());
            return "redirect:/user/doctors";
        }
    }

    /**
     * View profile
     */
    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails principal, Model model) {
        System.out.println("🔍 USER: Viewing profile");
        
        User user = userRepository.findByEmail(principal.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "user_profile";
    }
}
