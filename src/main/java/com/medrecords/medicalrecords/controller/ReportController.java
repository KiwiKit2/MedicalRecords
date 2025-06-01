package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
