package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.Doctor;
import com.medrecords.medicalrecords.model.Patient;
import com.medrecords.medicalrecords.repository.DoctorRepository;
import com.medrecords.medicalrecords.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        return "patients";
    }

    @PostMapping
    public String addPatient(@RequestParam String name, @RequestParam String egn,
                              @RequestParam Long primaryDoctorId,
                              @RequestParam(required = false) boolean paidInsuranceLast6Months) {
        Doctor primaryDoctor = doctorRepository.findById(primaryDoctorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor Id:" + primaryDoctorId));
        Patient patient = new Patient();
        patient.setName(name);
        patient.setEgn(egn);
        patient.setPrimaryDoctor(primaryDoctor);
        patient.setPaidInsuranceLast6Months(paidInsuranceLast6Months);
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
    public String updatePatient(@PathVariable Long id, @RequestParam String name, @RequestParam String egn,
                                 @RequestParam Long primaryDoctorId,
                                 @RequestParam(required = false) boolean paidInsuranceLast6Months) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient Id:" + id));
        Doctor primaryDoctor = doctorRepository.findById(primaryDoctorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor Id:" + primaryDoctorId));
        patient.setName(name);
        patient.setEgn(egn);
        patient.setPrimaryDoctor(primaryDoctor);
        patient.setPaidInsuranceLast6Months(paidInsuranceLast6Months);
        patientRepository.save(patient);
        return "redirect:/patients";
    }
}
