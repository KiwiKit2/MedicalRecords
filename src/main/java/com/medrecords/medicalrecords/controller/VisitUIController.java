package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.*;
import com.medrecords.medicalrecords.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/visits")
public class VisitUIController {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @GetMapping
    public String listVisits(Model model, Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PATIENT"))) {
            // Show only the authenticated patient's visits
            var patients = patientRepository.findByEgn(authentication.getName());
            if (!patients.isEmpty()) {
                model.addAttribute("visits", visitRepository.findByPatientId(patients.get(0).getId()));
            } else {
                model.addAttribute("visits", List.of());
            }
        } else {
            model.addAttribute("visits", visitRepository.findAll());
        }
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("diagnoses", diagnosisRepository.findAll());
        return "visits";
    }

    @PostMapping
    public String addVisit(@RequestParam Long patientId, @RequestParam Long doctorId,
                           @RequestParam String visitDate, @RequestParam Long diagnosisId,
                           @RequestParam(required = false) String treatment) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient Id:" + patientId));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor Id:" + doctorId));
        Diagnosis diagnosis = diagnosisRepository.findById(diagnosisId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid diagnosis Id:" + diagnosisId));

        Visit visit = new Visit();
        visit.setPatient(patient);
        visit.setDoctor(doctor);
        visit.setVisitDate(LocalDate.parse(visitDate));
        visit.setDiagnosis(diagnosis);
        visit.setTreatment(treatment);
        visitRepository.save(visit);
        return "redirect:/visits";
    }

    @GetMapping("/delete/{id}")
    public String deleteVisit(@PathVariable Long id) {
        visitRepository.deleteById(id);
        return "redirect:/visits";
    }

    @GetMapping("/edit/{id}")
    public String editVisit(@PathVariable Long id, Model model) {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid visit Id:" + id));
        model.addAttribute("visit", visit);
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("diagnoses", diagnosisRepository.findAll());
        return "edit_visit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String updateVisit(@PathVariable Long id, @RequestParam Long patientId, @RequestParam Long doctorId,
                               @RequestParam String visitDate, @RequestParam Long diagnosisId,
                               @RequestParam(required = false) String treatment) {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid visit Id:" + id));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient Id:" + patientId));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor Id:" + doctorId));
        Diagnosis diagnosis = diagnosisRepository.findById(diagnosisId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid diagnosis Id:" + diagnosisId));

        visit.setPatient(patient);
        visit.setDoctor(doctor);
        visit.setVisitDate(LocalDate.parse(visitDate));
        visit.setDiagnosis(diagnosis);
        visit.setTreatment(treatment);
        visitRepository.save(visit);
        return "redirect:/visits";
    }
}
