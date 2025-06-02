package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.*;
import com.medrecords.medicalrecords.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        if (authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PATIENT"))) {
            // Show only the authenticated patient's visits
            var patients = patientRepository.findByEgn(authentication.getName());
            if (!patients.isEmpty()) {
                model.addAttribute("visits", visitRepository.findByPatientId(patients.get(0).getId()));
            } else {
                model.addAttribute("visits", List.of());
            }
        } else if (authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DOCTOR"))) {
            // Show only the authenticated doctor's visits
            var optDoc = doctorRepository.findByName(authentication.getName());
            if (optDoc.isPresent()) {
                model.addAttribute("visits", visitRepository.findByDoctorId(optDoc.get().getId()));
            } else {
                model.addAttribute("visits", List.of());
            }
        } else {
            model.addAttribute("visits", visitRepository.findAll());
        }
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("diagnoses", diagnosisRepository.findAll());
        // Initialize default Visit with empty nested objects for form binding
        Visit defaultVisit = new Visit();
        defaultVisit.setPatient(new Patient());
        defaultVisit.setDoctor(new Doctor());
        defaultVisit.setDiagnosis(new Diagnosis());
        model.addAttribute("visit", defaultVisit);
        return "visits";
    }

    @PostMapping
    public String addVisit(@Valid @ModelAttribute("visit") Visit visit,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("diagnoses", diagnosisRepository.findAll());
            return "visits";
        }
        // fetch and set associations
        Patient patient = patientRepository.findById(visit.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient Id:" + visit.getPatient().getId()));
        Doctor doctor = doctorRepository.findById(visit.getDoctor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor Id:" + visit.getDoctor().getId()));
        Diagnosis diagnosis = visit.getDiagnosis() != null && visit.getDiagnosis().getId() != null
                ? diagnosisRepository.findById(visit.getDiagnosis().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid diagnosis Id:" + visit.getDiagnosis().getId()))
                : null;
        visit.setPatient(patient);
        visit.setDoctor(doctor);
        visit.setDiagnosis(diagnosis);
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
    public String updateVisit(@PathVariable Long id,
                              @Valid @ModelAttribute("visit") Visit visit,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("diagnoses", diagnosisRepository.findAll());
            return "edit_visit";
        }
        // fetch existing and set associations
        Visit existing = visitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid visit Id:" + id));
        Patient patient = patientRepository.findById(visit.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient Id:" + visit.getPatient().getId()));
        Doctor doctor = doctorRepository.findById(visit.getDoctor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor Id:" + visit.getDoctor().getId()));
        Diagnosis diagnosis = visit.getDiagnosis() != null && visit.getDiagnosis().getId() != null
                ? diagnosisRepository.findById(visit.getDiagnosis().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid diagnosis Id:" + visit.getDiagnosis().getId()))
                : null;
        existing.setPatient(patient);
        existing.setDoctor(doctor);
        existing.setVisitDate(visit.getVisitDate());
        existing.setDiagnosis(diagnosis);
        existing.setTreatment(visit.getTreatment());
        visitRepository.save(existing);
        return "redirect:/visits";
    }
}
