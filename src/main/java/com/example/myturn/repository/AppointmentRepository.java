package com.example.myturn.repository;

import com.example.myturn.model.Appointment;
import com.example.myturn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    /**
     * Find all appointments for a specific patient
     */
    List<Appointment> findByPatient(User patient);
    
    /**
     * Find all appointments for a specific doctor
     */
    List<Appointment> findByDoctor(User doctor);
    
    /**
     * Find all appointments (for admin)
     */
    List<Appointment> findAll();
}
