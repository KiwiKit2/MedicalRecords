package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.Doctor;
import com.medrecords.medicalrecords.model.Patient;
import com.medrecords.medicalrecords.model.Visit;
import com.medrecords.medicalrecords.service.ReportService;
import com.medrecords.medicalrecords.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
public class ReportUIController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private VisitRepository visitRepository;

    // 3.a patients with given diagnosis
    @GetMapping("/patients-by-diagnosis")
    public String patientsByDiagnosisForm(Model model) {
        model.addAttribute("diagnoses", diagnosisRepository.findAll());
        return "patients_by_diagnosis";
    }

    @PostMapping("/patients-by-diagnosis")
    public String patientsByDiagnosisResult(@RequestParam Long diagnosisId, Model model) {
        List<Visit> visits = visitRepository.findByDiagnosisId(diagnosisId);
        List<Patient> patients = visits.stream()
                .map(Visit::getPatient)
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("diagnoses", diagnosisRepository.findAll());
        model.addAttribute("selectedDiagnosis", diagnosisRepository.findById(diagnosisId).orElse(null));
        model.addAttribute("patients", patients);
        return "patients_by_diagnosis";
    }

    // 3.f visits per patient
    @GetMapping("/visits-by-patient")
    public String visitsByPatientForm(Model model) {
        model.addAttribute("patients", patientRepository.findAll());
        return "visits_by_patient";
    }

    @PostMapping("/visits-by-patient")
    public String visitsByPatientResult(@RequestParam Long patientId, Model model) {
        List<Visit> visits = visitRepository.findByPatientId(patientId);
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("selectedPatient", patientRepository.findById(patientId).orElse(null));
        model.addAttribute("visits", visits);
        return "visits_by_patient";
    }

    // 3.g visits in period
    @GetMapping("/visits-in-period")
    public String visitsInPeriodForm() {
        return "visits_in_period";
    }

    @PostMapping("/visits-in-period")
    public String visitsInPeriodResult(@RequestParam String startDate, @RequestParam String endDate, Model model) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<Visit> visits = visitRepository.findByVisitDateBetween(start, end);
        model.addAttribute("visits", visits);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        return "visits_in_period";
    }

    // 3.h visits by doctor in period
    @GetMapping("/visits-by-doctor")
    public String visitsByDoctorForm(Model model) {
        model.addAttribute("doctors", doctorRepository.findAll());
        return "visits_by_doctor";
    }

    @PostMapping("/visits-by-doctor")
    public String visitsByDoctorResult(@RequestParam Long doctorId,
                                       @RequestParam String startDate,
                                       @RequestParam String endDate,
                                       Model model) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<Visit> visits = visitRepository.findByDoctorIdAndVisitDateBetween(doctorId, start, end);
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("selectedDoctor", doctorRepository.findById(doctorId).orElse(null));
        model.addAttribute("visits", visits);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        return "visits_by_doctor";
    }

    // 3.i month with most sick leaves
    @GetMapping("/month-most-sick-leaves")
    public String monthWithMostSickLeaves(Model model) {
        model.addAttribute("month", reportService.getMonthWithMostSickLeaves());
        return "month_most_sick_leaves";
    }

    // 3.j doctors with most sick leaves
    @GetMapping("/doctors-most-sick-leaves")
    public String doctorsWithMostSickLeaves(Model model) {
        List<Doctor> doctors = reportService.getDoctorsWithMostSickLeaves();
        model.addAttribute("doctors", doctors);
        return "doctors_most_sick_leaves";
    }

    // 3.b most frequent diagnoses
    @GetMapping("/most-frequent-diagnoses")
    public String mostFrequentDiagnoses(Model model) {
        Map<String, Long> diagnosisCounts = reportService.getDiagnosisCounts();
        model.addAttribute("diagnosisCounts", diagnosisCounts);
        return "most_frequent_diagnoses";
    }

    // 3.c patients grouped by assigned doctor
    @GetMapping("/patients-grouped-by-doctor")
    public String patientsGroupedByDoctor(Model model) {
        Map<Doctor, List<Patient>> grouped = reportService.getPatientsGroupedByDoctor();
        model.addAttribute("patientsGroupedByDoctor", grouped);
        return "patients_grouped_by_doctor";
    }

    // 3.d count of patients per doctor
    @GetMapping("/patients-per-doctor")
    public String patientsPerDoctor(Model model) {
        Map<String, Long> counts = reportService.getPatientsPerDoctor();
        model.addAttribute("patientsPerDoctor", counts);
        return "patients_per_doctor";
    }

    // 3.e count of visits per doctor
    @GetMapping("/visits-per-doctor")
    public String visitsPerDoctor(Model model) {
        Map<String, Long> counts = reportService.getVisitsPerDoctor();
        model.addAttribute("visitsPerDoctor", counts);
        return "visits_per_doctor";
    }
}
