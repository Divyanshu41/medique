package com.example.medique.controller;

import com.example.medique.model.User;
import com.example.medique.model.view.DoctorView;
import com.example.medique.model.view.HospitalView;
import com.example.medique.repository.AppointmentRepository;
import com.example.medique.repository.UserRepository;
import com.example.medique.service.AppointmentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserRepository users;
    private final AppointmentRepository appointments;
    private final AppointmentService appointmentService;

    public UserController(UserRepository users, AppointmentRepository appointments, AppointmentService appointmentService) {
        this.users = users;
        this.appointments = appointments;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/doctors")
    public String viewDoctors(@AuthenticationPrincipal UserDetails principal, Model model) {
        User patient = users.findByEmail(principal.getUsername()).orElse(null);
        if (patient == null) return "redirect:/login";
        model.addAttribute("user", patient);
        List<DoctorView> doctors = new ArrayList<>();
        for (User doctor : users.findByRole("DOCTOR")) {
            if (!Boolean.TRUE.equals(doctor.getIsActive())) continue;
            DoctorView view = new DoctorView();
            view.setId(doctor.getId()); view.setName(doctor.getName());
            view.setEmail(doctor.getEmail()); view.setPhone(doctor.getPhone());
            view.setSpecialization(doctor.getAddress());
            view.setHospital(new HospitalView("Clinic")); view.setWorkingHours("09:00-17:00");
            doctors.add(view);
        }
        model.addAttribute("doctors", doctors);
        return "user_doctors";
    }

    @GetMapping("/appointments")
    public String viewAppointments(@AuthenticationPrincipal UserDetails principal, Model model) {
        User patient = users.findByEmail(principal.getUsername()).orElse(null);
        if (patient == null) return "redirect:/login";
        model.addAttribute("user", patient);
        model.addAttribute("appointments", appointments.findByPatient(patient));
        return "my_appointments";
    }

    @GetMapping("/book-appointment/{doctorId}")
    public String showBookingForm(@PathVariable Long doctorId, @AuthenticationPrincipal UserDetails principal, Model model) {
        User patient = users.findByEmail(principal.getUsername()).orElse(null);
        User doctor = users.findById(doctorId).orElse(null);
        if (patient == null) return "redirect:/login";
        if (doctor == null || !"DOCTOR".equals(doctor.getRole()) || !Boolean.TRUE.equals(doctor.getIsActive()))
            return "redirect:/user/doctors";
        model.addAttribute("user", patient); model.addAttribute("doctorId", doctorId);
        return "book_appointment";
    }

    @PostMapping("/book-appointment/{doctorId}")
    public String processBooking(@PathVariable Long doctorId, @RequestParam("date") String date,
                                 @RequestParam("slot") String slot, @AuthenticationPrincipal UserDetails principal,
                                 RedirectAttributes redirect) {
        try {
            User patient = users.findByEmail(principal.getUsername()).orElse(null);
            if (patient == null) return "redirect:/login";
            appointmentService.book(patient, doctorId, LocalDate.parse(date), slot);
            redirect.addFlashAttribute("success", "Appointment booked successfully!");
            return "redirect:/user/appointments";
        } catch (DateTimeParseException | IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/user/book-appointment/" + doctorId;
        }
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails principal, Model model) {
        User patient = users.findByEmail(principal.getUsername()).orElse(null);
        if (patient == null) return "redirect:/login";
        model.addAttribute("user", patient);
        return "user_profile";
    }
}
