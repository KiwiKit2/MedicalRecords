package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.Doctor;
import com.medrecords.medicalrecords.model.Patient;
import com.medrecords.medicalrecords.repository.DoctorRepository;
import com.medrecords.medicalrecords.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patients")
public class PatientUIController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping
    public String listPatients(Model model) {
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        // prepare empty patient with a nested Doctor instance for form binding
        Patient patient = new Patient();
        patient.setPrimaryDoctor(new Doctor());
        model.addAttribute("patient", patient);
        return "patients";
    }

    @PostMapping
    public String addPatient(@Valid @ModelAttribute("patient") Patient patient,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("doctors", doctorRepository.findAll());
            return "patients";
        }
        patientRepository.save(patient);
        return "redirect:/patients";
    }

    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable Long id) {
        patientRepository.deleteById(id);
        return "redirect:/patients";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and #id == authentication.name)")
    public String editPatient(@PathVariable Long id, Model model) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient Id:" + id));
        model.addAttribute("patient", patient);
        model.addAttribute("doctors", doctorRepository.findAll());
        return "edit_patient";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and #id == authentication.name)")
    public String updatePatient(@PathVariable Long id,
                                @Valid @ModelAttribute("patient") Patient patient,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctors", doctorRepository.findAll());
            return "edit_patient";
        }
        patient.setId(id);
        patientRepository.save(patient);
        return "redirect:/patients";
    }
}
