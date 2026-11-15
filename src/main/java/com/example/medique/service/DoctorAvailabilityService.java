package com.example.medique.service;

import com.example.medique.model.Appointment;
import com.example.medique.model.DoctorAvailability;
import com.example.medique.model.DoctorLeave;
import com.example.medique.model.User;
import com.example.medique.repository.AppointmentRepository;
import com.example.medique.repository.DoctorAvailabilityRepository;
import com.example.medique.repository.DoctorLeaveRepository;
import com.example.medique.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DoctorAvailabilityService {
    private static final int APPOINTMENT_MINUTES = 60;
    private static final DateTimeFormatter SLOT_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final DoctorAvailabilityRepository availabilities;
    private final DoctorLeaveRepository leaves;
    private final AppointmentRepository appointments;
    private final UserRepository users;

    public DoctorAvailabilityService(DoctorAvailabilityRepository availabilities,
                                     DoctorLeaveRepository leaves,
                                     AppointmentRepository appointments,
                                     UserRepository users) {
        this.availabilities = availabilities;
        this.leaves = leaves;
        this.appointments = appointments;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<DoctorAvailability> getSchedules(User doctor) {
        return availabilities.findByDoctorOrderByDayOfWeekAsc(doctor);
    }

    @Transactional(readOnly = true)
    public List<DoctorLeave> getUpcomingLeaves(User doctor) {
        return leaves.findByDoctorAndLeaveDateGreaterThanEqualOrderByLeaveDateAsc(doctor, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public String getWorkingHoursSummary(User doctor) {
        List<DoctorAvailability> schedules = getSchedules(doctor);
        if (schedules.isEmpty()) return "Not added";
        List<String> labels = new ArrayList<>();
        for (DoctorAvailability schedule : schedules) {
            labels.add(schedule.getDayOfWeek() + " " + schedule.getStartTime().format(SLOT_FORMAT)
                    + "-" + schedule.getEndTime().format(SLOT_FORMAT));
        }
        return String.join(", ", labels);
    }

    @Transactional
    public DoctorAvailability saveAvailability(User doctor, DayOfWeek dayOfWeek,
                                                String start, String end,
                                                String breakStartValue, String breakEndValue) {
        doctor = lockDoctor(doctor);
        LocalTime startTime = parseTime(start, "Start time");
        LocalTime endTime = parseTime(end, "End time");
        LocalTime breakStart = optionalTime(breakStartValue, "Break start");
        LocalTime breakEnd = optionalTime(breakEndValue, "Break end");

        if (dayOfWeek == null) throw new IllegalArgumentException("Select a valid weekday.");
        if (!startTime.isBefore(endTime))
            throw new IllegalArgumentException("End time must be later than start time.");
        if ((breakStart == null) != (breakEnd == null))
            throw new IllegalArgumentException("Enter both break start and break end, or leave both empty.");
        if (breakStart != null && (!startTime.isBefore(breakStart)
                || !breakStart.isBefore(breakEnd) || !breakEnd.isBefore(endTime))) {
            throw new IllegalArgumentException("Break time must be inside the working hours.");
        }

        List<Appointment> futureAppointments =
                appointments.findByDoctorAndAppointmentDateGreaterThanEqualAndStatusNot(
                        doctor, LocalDate.now(), "CANCELLED");
        for (Appointment appointment : futureAppointments) {
            if (appointment.getAppointmentDate().getDayOfWeek() == dayOfWeek
                    && !fitsSchedule(appointment.getTimeSlot(), startTime, endTime, breakStart, breakEnd)) {
                throw new IllegalArgumentException("Cannot change these hours because a future appointment already exists on "
                        + appointment.getAppointmentDate() + " at " + appointment.getTimeSlot() + ".");
            }
        }

        DoctorAvailability schedule = availabilities.findByDoctorAndDayOfWeek(doctor, dayOfWeek)
                .orElseGet(DoctorAvailability::new);
        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setBreakStart(breakStart);
        schedule.setBreakEnd(breakEnd);
        return availabilities.save(schedule);
    }

    @Transactional
    public void removeAvailability(Long availabilityId, User doctor) {
        doctor = lockDoctor(doctor);
        DoctorAvailability schedule = availabilities.findById(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Weekly schedule not found."));
        if (schedule.getDoctor() == null || !schedule.getDoctor().getId().equals(doctor.getId()))
            throw new IllegalArgumentException("You are not allowed to change this schedule.");

        List<Appointment> futureAppointments =
                appointments.findByDoctorAndAppointmentDateGreaterThanEqualAndStatusNot(
                        doctor, LocalDate.now(), "CANCELLED");
        boolean hasBookings = futureAppointments.stream()
                .anyMatch(a -> a.getAppointmentDate().getDayOfWeek() == schedule.getDayOfWeek());
        if (hasBookings)
            throw new IllegalArgumentException("Cannot remove this weekday while future appointments exist.");
        availabilities.delete(schedule);
    }

    @Transactional
    public DoctorLeave addLeave(User doctor, LocalDate leaveDate, String reason) {
        doctor = lockDoctor(doctor);
        if (leaveDate == null || leaveDate.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Leave date cannot be in the past.");
        if (leaves.existsByDoctorAndLeaveDate(doctor, leaveDate))
            throw new IllegalArgumentException("A leave is already added for this date.");

        List<Appointment> booked =
                appointments.findByDoctorAndAppointmentDateAndStatusNot(doctor, leaveDate, "CANCELLED");
        if (!booked.isEmpty())
            throw new IllegalArgumentException("Cannot add leave because appointments already exist on this date.");

        DoctorLeave leave = new DoctorLeave();
        leave.setDoctor(doctor);
        leave.setLeaveDate(leaveDate);
        String safeReason = reason == null ? "" : reason.trim();
        if (safeReason.length() > 250) throw new IllegalArgumentException("Leave reason must be 250 characters or fewer.");
        leave.setReason(safeReason.isBlank() ? null : safeReason);
        return leaves.save(leave);
    }

    @Transactional
    public void removeLeave(Long leaveId, User doctor) {
        doctor = lockDoctor(doctor);
        DoctorLeave leave = leaves.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave entry not found."));
        if (leave.getDoctor() == null || !leave.getDoctor().getId().equals(doctor.getId()))
            throw new IllegalArgumentException("You are not allowed to change this leave.");
        leaves.delete(leave);
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableSlots(Long doctorId, LocalDate date) {
        if (doctorId == null || date == null || date.isBefore(LocalDate.now())) return List.of();
        User doctor = users.findById(doctorId).orElse(null);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())
                || !Boolean.TRUE.equals(doctor.getIsActive())) return List.of();
        return getAvailableSlots(doctor, date);
    }

    @Transactional(readOnly = true)
    public boolean isSlotAvailable(User doctor, LocalDate date, String slot) {
        if (date == null || slot == null) return false;
        return getAvailableSlots(doctor, date).contains(slot);
    }

    private List<String> getAvailableSlots(User doctor, LocalDate date) {
        if (leaves.existsByDoctorAndLeaveDate(doctor, date)) return List.of();
        DoctorAvailability schedule = availabilities.findByDoctorAndDayOfWeek(doctor, date.getDayOfWeek())
                .orElse(null);
        if (schedule == null) return List.of();

        List<Appointment> bookedAppointments =
                appointments.findByDoctorAndAppointmentDateAndStatusNot(doctor, date, "CANCELLED");
        Set<String> bookedSlots = new HashSet<>();
        for (Appointment appointment : bookedAppointments) bookedSlots.add(appointment.getTimeSlot());

        List<String> result = new ArrayList<>();
        LocalTime slotStart = schedule.getStartTime();
        while (!slotStart.plusMinutes(APPOINTMENT_MINUTES).isAfter(schedule.getEndTime())) {
            LocalTime slotEnd = slotStart.plusMinutes(APPOINTMENT_MINUTES);
            boolean overlapsBreak = schedule.getBreakStart() != null
                    && slotStart.isBefore(schedule.getBreakEnd())
                    && slotEnd.isAfter(schedule.getBreakStart());
            String slot = slotStart.format(SLOT_FORMAT);
            boolean alreadyPassed = date.equals(LocalDate.now()) && !slotStart.isAfter(LocalTime.now());
            if (!overlapsBreak && !alreadyPassed && !bookedSlots.contains(slot)) result.add(slot);
            slotStart = slotStart.plusMinutes(APPOINTMENT_MINUTES);
        }
        return result;
    }

    private boolean fitsSchedule(String slotValue, LocalTime start, LocalTime end,
                                 LocalTime breakStart, LocalTime breakEnd) {
        try {
            LocalTime slotStart = LocalTime.parse(slotValue);
            LocalTime slotEnd = slotStart.plusMinutes(APPOINTMENT_MINUTES);
            boolean overlapsBreak = breakStart != null && slotStart.isBefore(breakEnd)
                    && slotEnd.isAfter(breakStart);
            return !slotStart.isBefore(start) && !slotEnd.isAfter(end) && !overlapsBreak;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    private LocalTime parseTime(String value, String label) {
        try {
            if (value == null || value.isBlank()) throw new DateTimeParseException("Missing time", "", 0);
            return LocalTime.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(label + " is required in HH:mm format.");
        }
    }

    private LocalTime optionalTime(String value, String label) {
        if (value == null || value.isBlank()) return null;
        return parseTime(value, label);
    }

    private User lockDoctor(User doctor) {
        if (doctor == null || doctor.getId() == null)
            throw new IllegalArgumentException("An active doctor account is required.");
        User lockedDoctor = users.findByIdForUpdate(doctor.getId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor account not found."));
        if (!"DOCTOR".equals(lockedDoctor.getRole()) || !Boolean.TRUE.equals(lockedDoctor.getIsActive()))
            throw new IllegalArgumentException("An active doctor account is required.");
        return lockedDoctor;
    }
}
