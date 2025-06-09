package com.medrecords.medicalrecords.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "EGN is mandatory")
    @Size(min = 10, max = 10, message = "EGN must be exactly 10 characters")
    @Column(unique = true)
    private String egn;

    private boolean paidInsuranceLast6Months;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor primaryDoctor;

    @NotBlank(message = "Password is mandatory")
    private String password;

    /**
     * Legacy constructor for tests: id, name, egn, paidInsuranceLast6Months, primaryDoctor
     */
    public Patient(Long id, String name, String egn, boolean paidInsuranceLast6Months, Doctor primaryDoctor) {
        this.id = id;
        this.name = name;
        this.egn = egn;
        this.paidInsuranceLast6Months = paidInsuranceLast6Months;
        this.primaryDoctor = primaryDoctor;
    }

    public Doctor getPrimaryDoctor() {
        return primaryDoctor;
    }

    public void setPrimaryDoctor(Doctor primaryDoctor) {
        this.primaryDoctor = primaryDoctor;
    }
}
