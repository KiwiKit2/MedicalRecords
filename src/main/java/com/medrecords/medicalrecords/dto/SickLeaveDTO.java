package com.medrecords.medicalrecords.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Data Transfer Object for SickLeave entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SickLeaveDTO {
    private Long id;
    private Long patientId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationInDays;
    private Long doctorId;
}
