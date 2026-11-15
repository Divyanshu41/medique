package com.example.medique.service;

import com.example.medique.model.Appointment;
import com.example.medique.model.User;
import com.example.medique.repository.AppointmentRepository;
import com.example.medique.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Set;

@Service
public class AppointmentService {
    private final AppointmentRepository appointments;
    private final UserRepository users;
    private final DoctorAvailabilityService availabilityService;

    public AppointmentService(AppointmentRepository appointments, UserRepository users,
                              DoctorAvailabilityService availabilityService) {
        this.appointments = appointments;
        this.users = users;
        this.availabilityService = availabilityService;
    }

    @Transactional
    public Appointment book(User patient, Long doctorId, LocalDate date, String slot) {
        if (date == null || date.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Appointment date cannot be in the past.");

        // Lock the doctor row so two concurrent requests cannot reserve the same doctor's slot.
        User doctor = users.findByIdForUpdate(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));
        if (!"DOCTOR".equals(doctor.getRole()) || !Boolean.TRUE.equals(doctor.getIsActive()))
            throw new IllegalArgumentException("This doctor is not available.");
        if (!availabilityService.isSlotAvailable(doctor, date, slot))
            throw new IllegalArgumentException("This time slot is not available. Choose another slot.");
        if (appointments.existsByDoctorAndAppointmentDateAndTimeSlotAndStatusNot(doctor, date, slot, "CANCELLED"))
            throw new IllegalArgumentException("This time slot is already booked. Choose another slot.");

        // PENDING reserves the slot while waiting for the doctor to confirm it.
        Appointment appointment = new Appointment(patient, doctor, date, slot);
        appointment.setStatus("PENDING");
        return appointments.save(appointment);
    }

    @Transactional
    public Appointment cancelForPatient(Long appointmentId, User patient) {
        Appointment appointment = appointments.findByIdForUpdate(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
        if (appointment.getPatient() == null || !appointment.getPatient().getId().equals(patient.getId()))
            throw new IllegalArgumentException("You are not allowed to cancel this appointment.");
        if (!Set.of("PENDING", "CONFIRMED").contains(appointment.getStatus()))
            throw new IllegalArgumentException("Only pending or confirmed appointments can be cancelled.");
        if (isInPast(appointment.getAppointmentDate(), appointment.getTimeSlot()))
            throw new IllegalArgumentException("A past appointment cannot be cancelled.");
        appointment.setStatus("CANCELLED");
        return appointments.save(appointment);
    }

    @Transactional
    public Appointment transitionStatus(Long appointmentId, String targetStatus) {
        Appointment appointment = appointments.findByIdForUpdate(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
        String current = appointment.getStatus();
        boolean allowed = ("PENDING".equals(current) && Set.of("CONFIRMED", "CANCELLED").contains(targetStatus))
                || ("CONFIRMED".equals(current) && Set.of("COMPLETED", "CANCELLED").contains(targetStatus));
        if (!allowed) throw new IllegalArgumentException("Invalid appointment status transition.");
        appointment.setStatus(targetStatus);
        return appointments.save(appointment);
    }

    private boolean isInPast(LocalDate date, String slot) {
        if (date == null || date.isBefore(LocalDate.now())) return true;
        if (!date.equals(LocalDate.now())) return false;
        try {
            return !LocalTime.parse(slot).isAfter(LocalTime.now());
        } catch (DateTimeParseException ex) {
            return true;
        }
    }
}
