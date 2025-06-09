package com.medrecords.medicalrecords.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Diagnosis entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisDTO {
    private Long id;
    private String name;
    private String description;
}
