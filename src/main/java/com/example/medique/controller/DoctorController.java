package com.example.medique.controller;

import com.example.medique.model.Appointment;
import com.example.medique.model.User;
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

import java.time.DayOfWeek;
import java.time.LocalDate;

@Controller
@RequestMapping("/doctor")
public class DoctorController {
    private final UserRepository users;
    private final AppointmentRepository appointments;
    private final AppointmentService appointmentService;
    private final DoctorAvailabilityService availabilityService;

    public DoctorController(UserRepository users, AppointmentRepository appointments,
                             AppointmentService appointmentService,
                             DoctorAvailabilityService availabilityService) {
        this.users = users;
        this.appointments = appointments;
        this.appointmentService = appointmentService;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/appointments")
    public String appointments(@AuthenticationPrincipal UserDetails principal, Model model) {
        User doctor = users.findByEmail(principal.getUsername()).orElse(null);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) return "redirect:/login";
        model.addAttribute("user", doctor);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("appointments", appointments.findByDoctor(doctor));
        model.addAttribute("weeklySchedules", availabilityService.getSchedules(doctor));
        model.addAttribute("upcomingLeaves", availabilityService.getUpcomingLeaves(doctor));
        return "doctor_dashboard";
    }

    @PostMapping("/availability")
    public String saveAvailability(@RequestParam String dayOfWeek,
                                   @RequestParam String startTime,
                                   @RequestParam String endTime,
                                   @RequestParam(required = false) String breakStart,
                                   @RequestParam(required = false) String breakEnd,
                                   @AuthenticationPrincipal UserDetails principal,
                                   RedirectAttributes redirect) {
        User doctor = users.findByEmail(principal.getUsername()).orElse(null);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) return "redirect:/login";
        try {
            DayOfWeek day = DayOfWeek.valueOf(dayOfWeek.trim().toUpperCase());
            availabilityService.saveAvailability(doctor, day, startTime, endTime, breakStart, breakEnd);
            redirect.addFlashAttribute("success", "Weekly availability saved.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/doctor/appointments";
    }

    @PostMapping("/availability/{id}/delete")
    public String removeAvailability(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails principal,
                                     RedirectAttributes redirect) {
        User doctor = users.findByEmail(principal.getUsername()).orElse(null);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) return "redirect:/login";
        try {
            availabilityService.removeAvailability(id, doctor);
            redirect.addFlashAttribute("success", "Weekly availability removed.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/doctor/appointments";
    }

    @PostMapping("/leaves")
    public String addLeave(@RequestParam LocalDate leaveDate,
                           @RequestParam(required = false) String reason,
                           @AuthenticationPrincipal UserDetails principal,
                           RedirectAttributes redirect) {
        User doctor = users.findByEmail(principal.getUsername()).orElse(null);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) return "redirect:/login";
        try {
            availabilityService.addLeave(doctor, leaveDate, reason);
            redirect.addFlashAttribute("success", "Leave date added. Patients cannot book this day.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/doctor/appointments";
    }

    @PostMapping("/leaves/{id}/delete")
    public String removeLeave(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails principal,
                              RedirectAttributes redirect) {
        User doctor = users.findByEmail(principal.getUsername()).orElse(null);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) return "redirect:/login";
        try {
            availabilityService.removeLeave(id, doctor);
            redirect.addFlashAttribute("success", "Leave date removed.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/doctor/appointments";
    }

    @PostMapping("/appointments/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status,
            @AuthenticationPrincipal UserDetails principal, RedirectAttributes redirect) {
        User doctor = users.findByEmail(principal.getUsername()).orElse(null);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) return "redirect:/login";
        Appointment appointment = appointments.findById(id).orElse(null);
        if (appointment == null || appointment.getDoctor() == null
                || !appointment.getDoctor().getId().equals(doctor.getId())) {
            redirect.addFlashAttribute("error", "Appointment not found.");
            return "redirect:/doctor/appointments";
        }
        try {
            appointmentService.transitionStatus(id, status);
            redirect.addFlashAttribute("success", "Appointment status updated.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/doctor/appointments";
    }
}
