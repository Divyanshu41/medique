package com.example.medique.repository;

import com.example.medique.model.Appointment;
import com.example.medique.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(User patient);
    List<Appointment> findByDoctor(User doctor);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Appointment a where a.id = :id")
    Optional<Appointment> findByIdForUpdate(@Param("id") Long id);

    boolean existsByDoctorAndAppointmentDateAndTimeSlotAndStatusNot(
            User doctor, LocalDate appointmentDate, String timeSlot, String status);

    List<Appointment> findByDoctorAndAppointmentDateAndStatusNot(
            User doctor, LocalDate appointmentDate, String status);

    List<Appointment> findByDoctorAndAppointmentDateGreaterThanEqualAndStatusNot(
            User doctor, LocalDate appointmentDate, String status);
}
