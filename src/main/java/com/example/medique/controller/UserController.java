package com.example.medique.controller;

import com.example.medique.model.User;
import com.example.medique.model.view.DoctorView;
import com.example.medique.model.view.HospitalView;
import com.example.medique.repository.AppointmentRepository;
import com.example.medique.repository.UserRepository;
import com.example.medique.service.AppointmentService;
import com.example.medique.service.DoctorAvailabilityService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserRepository users;
    private final AppointmentRepository appointments;
    private final AppointmentService appointmentService;
    private final DoctorAvailabilityService availabilityService;

    public UserController(UserRepository users, AppointmentRepository appointments,
                          AppointmentService appointmentService,
                          DoctorAvailabilityService availabilityService) {
        this.users = users;
        this.appointments = appointments;
        this.appointmentService = appointmentService;
        this.availabilityService = availabilityService;
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
            view.setHospital(new HospitalView("Clinic"));
            view.setWorkingHours(availabilityService.getWorkingHoursSummary(doctor));
            doctors.add(view);
        }
        model.addAttribute("doctors", doctors);
        return "user_doctors";
    }

    @GetMapping("/doctors/{doctorId}/available-slots")
    @ResponseBody
    public List<String> availableSlots(@PathVariable Long doctorId, @RequestParam LocalDate date) {
        return availabilityService.getAvailableSlots(doctorId, date);
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
        model.addAttribute("user", patient);
        model.addAttribute("doctorId", doctorId);
        model.addAttribute("doctorName", doctor.getName());
        model.addAttribute("minDate", LocalDate.now());
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
            redirect.addFlashAttribute("success", "Appointment request submitted. Waiting for doctor confirmation.");
            return "redirect:/user/appointments";
        } catch (DateTimeException | IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/user/book-appointment/" + doctorId;
        }
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal,
            RedirectAttributes redirect) {
        try {
            User patient = users.findByEmail(principal.getUsername()).orElse(null);
            if (patient == null) return "redirect:/login";
            appointmentService.cancelForPatient(id, patient);
            redirect.addFlashAttribute("success", "Appointment cancelled successfully.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/user/appointments";
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails principal, Model model) {
        User patient = users.findByEmail(principal.getUsername()).orElse(null);
        if (patient == null) return "redirect:/login";
        model.addAttribute("user", patient);
        return "user_profile";
    }
}
