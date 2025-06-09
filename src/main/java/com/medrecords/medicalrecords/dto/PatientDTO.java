package com.medrecords.medicalrecords.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Patient entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDTO {
    private Long id;
    private String name;
    private String egn;
    private boolean paidInsuranceLast6Months;
    private Long primaryDoctorId;
}
