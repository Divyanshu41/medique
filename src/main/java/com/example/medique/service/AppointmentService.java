package com.example.medique.service;

import com.example.medique.model.Appointment;
import com.example.medique.model.User;
import com.example.medique.repository.AppointmentRepository;
import com.example.medique.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Set;

@Service
public class AppointmentService {
    private static final Set<String> SLOTS = Set.of("09:00","10:00","11:00","12:00","14:00","15:00","16:00");
    private final AppointmentRepository appointments;
    private final UserRepository users;

    public AppointmentService(AppointmentRepository appointments, UserRepository users) {
        this.appointments = appointments;
        this.users = users;
    }

    @Transactional
    public Appointment book(User patient, Long doctorId, LocalDate date, String slot) {
        if (date == null || date.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Appointment date cannot be in the past.");
        if (!SLOTS.contains(slot))
            throw new IllegalArgumentException("Please select a valid appointment time slot.");

        // Serialize simultaneous bookings for the same doctor.
        User doctor = users.findByIdForUpdate(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));
        if (!"DOCTOR".equals(doctor.getRole()) || !Boolean.TRUE.equals(doctor.getIsActive()))
            throw new IllegalArgumentException("This doctor is not available.");
        if (appointments.existsByDoctorAndAppointmentDateAndTimeSlotAndStatusNot(doctor, date, slot, "CANCELLED"))
            throw new IllegalArgumentException("This time slot is already booked. Choose another slot.");

        Appointment appointment = new Appointment(patient, doctor, date, slot);
        appointment.setStatus("CONFIRMED");
        return appointments.save(appointment);
    }
}
