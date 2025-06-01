package com.medrecords.medicalrecords.service;

import com.medrecords.medicalrecords.repository.DiagnosisRepository;
import com.medrecords.medicalrecords.repository.DoctorRepository;
import com.medrecords.medicalrecords.repository.PatientRepository;
import com.medrecords.medicalrecords.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ReportServiceTest {

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDiagnosisCounts() {
        when(visitRepository.findAll()).thenReturn(Collections.emptyList());
        Map<String, Long> result = reportService.getDiagnosisCounts();
        assertEquals(0, result.size());
    }

    @Test
    void testGetPatientsPerDoctor() {
        when(patientRepository.findAll()).thenReturn(Collections.emptyList());
        Map<String, Long> result = reportService.getPatientsPerDoctor();
        assertEquals(0, result.size());
    }

    @Test
    void testGetVisitsPerDoctor() {
        when(visitRepository.findAll()).thenReturn(Collections.emptyList());
        Map<String, Long> result = reportService.getVisitsPerDoctor();
        assertEquals(0, result.size());
    }
}
