package com.medrecords.medicalrecords.model;

import jakarta.persistence.*;
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

    private String name;

    @Column(unique = true)
    private String egn;

    private boolean paidInsuranceLast6Months;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor primaryDoctor;

    public Doctor getPrimaryDoctor() {
        return primaryDoctor;
    }

    public void setPrimaryDoctor(Doctor primaryDoctor) {
        this.primaryDoctor = primaryDoctor;
    }
}
