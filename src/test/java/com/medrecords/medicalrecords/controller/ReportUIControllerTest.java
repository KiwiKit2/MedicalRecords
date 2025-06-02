package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.Diagnosis;
import com.medrecords.medicalrecords.model.Patient;
import com.medrecords.medicalrecords.model.Doctor;
import com.medrecords.medicalrecords.model.Visit;
import com.medrecords.medicalrecords.repository.DiagnosisRepository;
import com.medrecords.medicalrecords.repository.PatientRepository;
import com.medrecords.medicalrecords.repository.DoctorRepository;
import com.medrecords.medicalrecords.repository.VisitRepository;
import com.medrecords.medicalrecords.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportUIController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportUIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @MockBean
    private DiagnosisRepository diagnosisRepository;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private DoctorRepository doctorRepository;

    @MockBean
    private VisitRepository visitRepository;

    @Test
    void patientsByDiagnosisForm_ShouldRenderForm() throws Exception {
        when(diagnosisRepository.findAll()).thenReturn(List.of(new Diagnosis(1L, "Flu", "desc")));
        mockMvc.perform(get("/reports/patients-by-diagnosis"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients_by_diagnosis"))
                .andExpect(model().attributeExists("diagnoses"));
    }

    @Test
    void patientsByDiagnosisResult_ShouldShowResults() throws Exception {
        var diag = new Diagnosis(1L, "Flu", "desc");
        var visit = new Visit(1L, new Patient(1L, "John", "1234567890", false, null), null, LocalDate.now(), diag, "treat");
        when(diagnosisRepository.findAll()).thenReturn(List.of(diag));
        when(visitRepository.findByDiagnosisId(1L)).thenReturn(List.of(visit));
        when(diagnosisRepository.findById(1L)).thenReturn(Optional.of(diag));
        mockMvc.perform(post("/reports/patients-by-diagnosis").param("diagnosisId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients_by_diagnosis"))
                .andExpect(model().attributeExists("diagnoses", "selectedDiagnosis", "patients"));
    }

    @Test
    void visitsByPatientForm_ShouldRenderForm() throws Exception {
        when(patientRepository.findAll()).thenReturn(List.of(new Patient(1L, "Jane", "9876543210", false, null)));
        mockMvc.perform(get("/reports/visits-by-patient"))
                .andExpect(status().isOk())
                .andExpect(view().name("visits_by_patient"))
                .andExpect(model().attributeExists("patients"));
    }

    @Test
    void visitsByPatientResult_ShouldShowResults() throws Exception {
        var pat = new Patient(1L, "Jane", "9876543210", false, null);
        var visit = new Visit(2L, pat, null, LocalDate.now(), null, "treatment");
        when(patientRepository.findAll()).thenReturn(List.of(pat));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(pat));
        when(visitRepository.findByPatientId(1L)).thenReturn(List.of(visit));
        mockMvc.perform(post("/reports/visits-by-patient").param("patientId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("visits_by_patient"))
                .andExpect(model().attributeExists("patients", "selectedPatient", "visits"));
    }

    @Test
    void visitsInPeriodForm_ShouldRenderForm() throws Exception {
        mockMvc.perform(get("/reports/visits-in-period"))
                .andExpect(status().isOk())
                .andExpect(view().name("visits_in_period"));
    }

    @Test
    void visitsInPeriodResult_ShouldShowResults() throws Exception {
        var start = "2025-01-01";
        var end = "2025-02-01";
        when(visitRepository.findByVisitDateBetween(LocalDate.parse(start), LocalDate.parse(end)))
                .thenReturn(List.of(new Visit(3L, null, null, LocalDate.parse(start), null, null)));
        mockMvc.perform(post("/reports/visits-in-period").param("startDate", start).param("endDate", end))
                .andExpect(status().isOk())
                .andExpect(view().name("visits_in_period"))
                .andExpect(model().attributeExists("visits", "startDate", "endDate"));
    }

    @Test
    void visitsByDoctorForm_ShouldRenderForm() throws Exception {
        when(doctorRepository.findAll()).thenReturn(List.of(new Doctor(1L, "Dr A", null, null)));
        mockMvc.perform(get("/reports/visits-by-doctor"))
                .andExpect(status().isOk())
                .andExpect(view().name("visits_by_doctor"))
                .andExpect(model().attributeExists("doctors"));
    }

    @Test
    void visitsByDoctorResult_ShouldShowResults() throws Exception {
        var doc = new Doctor(1L, "Dr A", null, null);
        var start = "2025-01-01";
        var end = "2025-02-01";
        var visit = new Visit(4L, null, doc, LocalDate.parse(start), null, null);
        when(doctorRepository.findAll()).thenReturn(List.of(doc));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(visitRepository.findByDoctorIdAndVisitDateBetween(1L, LocalDate.parse(start), LocalDate.parse(end)))
                .thenReturn(List.of(visit));
        mockMvc.perform(post("/reports/visits-by-doctor")
                        .param("doctorId", "1")
                        .param("startDate", start)
                        .param("endDate", end))
                .andExpect(status().isOk())
                .andExpect(view().name("visits_by_doctor"))
                .andExpect(model().attributeExists("doctors", "selectedDoctor", "visits", "startDate", "endDate"));
    }

    @Test
    void monthWithMostSickLeaves_ShouldRenderView() throws Exception {
        when(reportService.getMonthWithMostSickLeaves()).thenReturn("January");
        mockMvc.perform(get("/reports/month-most-sick-leaves"))
                .andExpect(status().isOk())
                .andExpect(view().name("month_most_sick_leaves"))
                .andExpect(model().attributeExists("month"));
    }

    @Test
    void doctorsWithMostSickLeaves_ShouldRenderView() throws Exception {
        when(reportService.getDoctorsWithMostSickLeaves())
                .thenReturn(List.of(new Doctor(2L, "Dr B", null, null)));
        mockMvc.perform(get("/reports/doctors-most-sick-leaves"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors_most_sick_leaves"))
                .andExpect(model().attributeExists("doctors"));
    }
}
