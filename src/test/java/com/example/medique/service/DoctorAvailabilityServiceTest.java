package com.example.medique.service;

import com.example.medique.model.Appointment;
import com.example.medique.model.DoctorAvailability;
import com.example.medique.model.User;
import com.example.medique.repository.AppointmentRepository;
import com.example.medique.repository.DoctorAvailabilityRepository;
import com.example.medique.repository.DoctorLeaveRepository;
import com.example.medique.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorAvailabilityServiceTest {
    @Mock private DoctorAvailabilityRepository availabilities;
    @Mock private DoctorLeaveRepository leaves;
    @Mock private AppointmentRepository appointments;
    @Mock private UserRepository users;
    @InjectMocks private DoctorAvailabilityService service;

    @Test
    void availableSlotsRespectWorkingHoursBreaksAndExistingBookings() {
        User doctor = new User();
        doctor.setId(12L);
        doctor.setRole("DOCTOR");
        doctor.setIsActive(true);
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        DoctorAvailability schedule = new DoctorAvailability();
        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(9, 0));
        schedule.setEndTime(LocalTime.of(13, 0));
        schedule.setBreakStart(LocalTime.of(11, 0));
        schedule.setBreakEnd(LocalTime.of(12, 0));

        Appointment booked = new Appointment(new User(), doctor, monday, "09:00");
        when(users.findById(12L)).thenReturn(Optional.of(doctor));
        when(leaves.existsByDoctorAndLeaveDate(doctor, monday)).thenReturn(false);
        when(availabilities.findByDoctorAndDayOfWeek(doctor, DayOfWeek.MONDAY))
                .thenReturn(Optional.of(schedule));
        when(appointments.findByDoctorAndAppointmentDateAndStatusNot(doctor, monday, "CANCELLED"))
                .thenReturn(List.of(booked));

        assertEquals(List.of("10:00", "12:00"), service.getAvailableSlots(12L, monday));
    }


    @Test
    void scheduleRejectsBreakOutsideWorkingHours() {
        User doctor = new User();
        doctor.setId(12L);
        doctor.setRole("DOCTOR");
        doctor.setIsActive(true);
        when(users.findByIdForUpdate(12L)).thenReturn(Optional.of(doctor));

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.saveAvailability(doctor, DayOfWeek.MONDAY,
                        "09:00", "17:00", "16:00", "18:00"));
        org.mockito.Mockito.verify(availabilities, org.mockito.Mockito.never())
                .save(org.mockito.ArgumentMatchers.any(DoctorAvailability.class));
    }

    @Test
    void leaveCannotBeAddedWhenAppointmentsAlreadyExist() {
        User doctor = new User();
        doctor.setId(12L);
        doctor.setRole("DOCTOR");
        doctor.setIsActive(true);
        LocalDate leaveDate = LocalDate.now().plusDays(3);
        Appointment booked = new Appointment(new User(), doctor, leaveDate, "10:00");
        when(users.findByIdForUpdate(12L)).thenReturn(Optional.of(doctor));
        when(leaves.existsByDoctorAndLeaveDate(doctor, leaveDate)).thenReturn(false);
        when(appointments.findByDoctorAndAppointmentDateAndStatusNot(doctor, leaveDate, "CANCELLED"))
                .thenReturn(List.of(booked));

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.addLeave(doctor, leaveDate, "Personal leave"));
        org.mockito.Mockito.verify(leaves, org.mockito.Mockito.never())
                .save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void leaveDateReturnsNoSlots() {
        User doctor = new User();
        doctor.setId(12L);
        doctor.setRole("DOCTOR");
        doctor.setIsActive(true);
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        when(users.findById(12L)).thenReturn(Optional.of(doctor));
        when(leaves.existsByDoctorAndLeaveDate(doctor, monday)).thenReturn(true);

        assertEquals(List.of(), service.getAvailableSlots(12L, monday));
    }
}
