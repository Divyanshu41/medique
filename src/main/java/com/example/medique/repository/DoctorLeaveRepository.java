package com.example.medique.repository;

import com.example.medique.model.DoctorLeave;
import com.example.medique.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorLeaveRepository extends JpaRepository<DoctorLeave, Long> {
    List<DoctorLeave> findByDoctorAndLeaveDateGreaterThanEqualOrderByLeaveDateAsc(User doctor, LocalDate date);
    Optional<DoctorLeave> findByDoctorAndLeaveDate(User doctor, LocalDate date);
    boolean existsByDoctorAndLeaveDate(User doctor, LocalDate date);
}
