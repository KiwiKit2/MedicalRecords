package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.Doctor;
import com.medrecords.medicalrecords.model.Visit;
import com.medrecords.medicalrecords.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/diagnosis-counts")
    public Map<String, Long> getDiagnosisCounts() {
        return reportService.getDiagnosisCounts();
    }

    @GetMapping("/patients-per-doctor")
    public Map<String, Long> getPatientsPerDoctor() {
        return reportService.getPatientsPerDoctor();
    }

    @GetMapping("/visits-per-doctor")
    public Map<String, Long> getVisitsPerDoctor() {
        return reportService.getVisitsPerDoctor();
    }

    @GetMapping("/visits-for-patient")
    public List<Visit> getVisitsForPatient(@RequestParam Long patientId) {
        return reportService.getVisitsForPatient(patientId);
    }

    @GetMapping("/visits-for-period")
    public List<Visit> getVisitsForPeriod(@RequestParam String startDate, @RequestParam String endDate) {
        return reportService.getVisitsForPeriod(LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @GetMapping("/visits-for-doctor-period")
    public List<Visit> getVisitsForDoctorInPeriod(@RequestParam Long doctorId, @RequestParam String startDate, @RequestParam String endDate) {
        return reportService.getVisitsForDoctorInPeriod(doctorId, LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @GetMapping("/month-most-sick-leaves")
    public String getMonthWithMostSickLeaves() {
        return reportService.getMonthWithMostSickLeaves();
    }

    @GetMapping("/doctor-most-sick-leaves")
    public List<Doctor> getDoctorsWithMostSickLeaves() {
        return reportService.getDoctorsWithMostSickLeaves();
    }
}
