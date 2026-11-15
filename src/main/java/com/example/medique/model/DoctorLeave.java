package com.example.medique.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "doctor_leaves", uniqueConstraints = {
        @UniqueConstraint(name = "uk_doctor_leave_date", columnNames = {"doctor_id", "leave_date"})
})
public class DoctorLeave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(name = "leave_date", nullable = false)
    private LocalDate leaveDate;

    @Column(length = 250)
    private String reason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getDoctor() { return doctor; }
    public void setDoctor(User doctor) { this.doctor = doctor; }
    public LocalDate getLeaveDate() { return leaveDate; }
    public void setLeaveDate(LocalDate leaveDate) { this.leaveDate = leaveDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
