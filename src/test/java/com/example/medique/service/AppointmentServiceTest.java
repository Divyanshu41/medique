package com.example.medique.service;

import com.example.medique.model.Appointment;
import com.example.medique.model.User;
import com.example.medique.repository.AppointmentRepository;
import com.example.medique.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
    @Mock private AppointmentRepository appointments;
    @Mock private UserRepository users;
    @Mock private DoctorAvailabilityService availabilityService;
    @InjectMocks private AppointmentService service;

    @Test
    void bookingRejectsPastDateBeforeDatabaseAccess() {
        User patient = new User();
        assertThrows(IllegalArgumentException.class,
                () -> service.book(patient, 12L, LocalDate.now().minusDays(1), "09:00"));
        verifyNoInteractions(users, appointments);
    }

    @Test
    void bookingCreatesPendingAppointmentAndReservesSlot() {
        User patient = new User();
        patient.setId(4L);
        User doctor = new User();
        doctor.setId(12L);
        doctor.setRole("DOCTOR");
        doctor.setIsActive(true);
        when(users.findByIdForUpdate(12L)).thenReturn(Optional.of(doctor));
        when(availabilityService.isSlotAvailable(doctor, LocalDate.now().plusDays(1), "09:00")).thenReturn(true);
        when(appointments.existsByDoctorAndAppointmentDateAndTimeSlotAndStatusNot(
                doctor, LocalDate.now().plusDays(1), "09:00", "CANCELLED")).thenReturn(false);
        when(appointments.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment saved = service.book(patient, 12L, LocalDate.now().plusDays(1), "09:00");

        assertEquals("PENDING", saved.getStatus());
        assertSame(patient, saved.getPatient());
        assertSame(doctor, saved.getDoctor());
        verify(appointments).save(any(Appointment.class));
    }

    @Test
    void bookingRejectsSlotOutsideDoctorsAvailability() {
        User patient = new User();
        User doctor = new User();
        doctor.setId(12L);
        doctor.setRole("DOCTOR");
        doctor.setIsActive(true);
        LocalDate date = LocalDate.now().plusDays(1);
        when(users.findByIdForUpdate(12L)).thenReturn(Optional.of(doctor));
        when(availabilityService.isSlotAvailable(doctor, date, "09:00")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> service.book(patient, 12L, date, "09:00"));
        verify(appointments, never()).save(any(Appointment.class));
    }

    @Test
    void patientCannotCancelAnotherPatientsAppointment() {
        User owner = new User();
        owner.setId(1L);
        User otherPatient = new User();
        otherPatient.setId(2L);
        Appointment appointment = new Appointment(owner, new User(), LocalDate.now().plusDays(1), "09:00");
        appointment.setId(5L);
        when(appointments.findByIdForUpdate(5L)).thenReturn(Optional.of(appointment));

        assertThrows(IllegalArgumentException.class,
                () -> service.cancelForPatient(5L, otherPatient));
        verify(appointments, never()).save(any(Appointment.class));
    }

    @Test
    void completedAppointmentCannotBeCancelledOrReopened() {
        Appointment appointment = new Appointment(new User(), new User(), LocalDate.now().plusDays(1), "09:00");
        appointment.setStatus("COMPLETED");
        when(appointments.findByIdForUpdate(8L)).thenReturn(Optional.of(appointment));

        assertThrows(IllegalArgumentException.class,
                () -> service.transitionStatus(8L, "CANCELLED"));
        verify(appointments, never()).save(any(Appointment.class));
    }
}
