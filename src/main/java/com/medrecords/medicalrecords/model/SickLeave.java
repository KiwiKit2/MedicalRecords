package com.medrecords.medicalrecords.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "SICKLEAVE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SickLeave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient is mandatory")
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Start date is mandatory")
    private LocalDate startDate;

    // endDate is computed
    private LocalDate endDate;

    @NotNull(message = "Duration is mandatory")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationInDays;

    @NotNull(message = "Doctor is mandatory")
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
        if (this.startDate != null) {
            this.endDate = this.startDate.plusDays(durationInDays);
        }
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        if (this.durationInDays != null && this.durationInDays > 0) {
            this.endDate = startDate.plusDays(this.durationInDays);
        }
    }
}
