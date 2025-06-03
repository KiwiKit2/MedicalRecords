package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.*;
import com.medrecords.medicalrecords.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
            // Patient sees only own visits
            var patients = patientRepository.findByEgn(authentication.getName());
            model.addAttribute("visits", patients.isEmpty()
                    ? List.of()
                    : visitRepository.findByPatientId(patients.get(0).getId()));
        } else {
            // Admin and Doctor see all visits
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

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PostMapping
    public String addVisit(@Valid @ModelAttribute("visit") Visit visit,
                           BindingResult bindingResult,
                           Model model,
                           Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("diagnoses", diagnosisRepository.findAll());
            return "visits";
        }
        // fetch and set associations
        Patient patient = patientRepository.findById(visit.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient Id:" + visit.getPatient().getId()));
        // if doctor user, force doctor to be current user
        Doctor doctor;
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DOCTOR"))) {
            doctor = doctorRepository.findByName(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid doctor username:" + authentication.getName()));
        } else {
            doctor = doctorRepository.findById(visit.getDoctor().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid doctor Id:" + visit.getDoctor().getId()));
        }
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

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @GetMapping("/delete/{id}")
    public String deleteVisit(@PathVariable Long id) {
        visitRepository.deleteById(id);
        return "redirect:/visits";
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @GetMapping("/edit/{id}")
    public String editVisit(@PathVariable Long id, Model model, Authentication authentication) {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid visit Id:" + id));
        // doctor can edit only own visits
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DOCTOR")) 
                && !visit.getDoctor().getName().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        model.addAttribute("visit", visit);
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("diagnoses", diagnosisRepository.findAll());
        return "edit_visit";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @PostMapping("/edit/{id}")
    public String updateVisit(@PathVariable Long id,
                          @Valid @ModelAttribute("visit") Visit visit,
                          BindingResult bindingResult,
                          Model model,
                          Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("doctors", doctorRepository.findAll());
            model.addAttribute("diagnoses", diagnosisRepository.findAll());
            return "edit_visit";
        }
        Visit existing = visitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid visit Id:" + id));
        // doctor can update only own visits
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DOCTOR")) 
                && !existing.getDoctor().getName().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        // fetch existing and set associations
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
