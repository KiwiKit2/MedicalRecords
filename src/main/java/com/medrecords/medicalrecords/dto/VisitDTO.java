package com.medrecords.medicalrecords.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Data Transfer Object for Visit entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDate visitDate;
    private Long diagnosisId;
    private String treatment;
}
