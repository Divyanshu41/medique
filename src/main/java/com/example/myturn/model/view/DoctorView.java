package com.example.myturn.model.view;

public class DoctorView {
    private Long id;
    private String name;
    private String email;
    private String specialization;
    private String phone;
    private HospitalView hospital;
    private String workingHours;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public HospitalView getHospital() { return hospital; }
    public void setHospital(HospitalView hospital) { this.hospital = hospital; }

    public String getWorkingHours() { return workingHours; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }
}
