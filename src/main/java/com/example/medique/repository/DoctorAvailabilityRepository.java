package com.example.medique.repository;

import com.example.medique.model.DoctorAvailability;
import com.example.medique.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    List<DoctorAvailability> findByDoctorOrderByDayOfWeekAsc(User doctor);
    Optional<DoctorAvailability> findByDoctorAndDayOfWeek(User doctor, DayOfWeek dayOfWeek);
}
