package com.example.medique.repository;

import com.example.medique.model.Appointment;
import com.example.medique.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(User patient);
    List<Appointment> findByDoctor(User doctor);
    boolean existsByDoctorAndAppointmentDateAndTimeSlotAndStatusNot(
            User doctor, LocalDate appointmentDate, String timeSlot, String status);
}
