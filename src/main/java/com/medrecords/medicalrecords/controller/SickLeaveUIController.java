package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.*;
import com.medrecords.medicalrecords.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/sick-leaves")
public class SickLeaveUIController {

    @Autowired
    private SickLeaveRepository sickLeaveRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping
    public String listSickLeaves(Model model) {
        model.addAttribute("sickLeaves", sickLeaveRepository.findAll());
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("sickLeave", new com.medrecords.medicalrecords.model.SickLeave());
        return "sick_leaves";
    }

    @PostMapping
    public String addSickLeave(@Valid @ModelAttribute("sickLeave") SickLeave sickLeave,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("sickLeaves", sickLeaveRepository.findAll());
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("doctors", doctorRepository.findAll());
            return "sick_leaves";
        }
        // ensure associations
        Patient patient = patientRepository.findById(sickLeave.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient Id:" + sickLeave.getPatient().getId()));
        Doctor doctor = doctorRepository.findById(sickLeave.getDoctor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor Id:" + sickLeave.getDoctor().getId()));
        sickLeave.setPatient(patient);
        sickLeave.setDoctor(doctor);
        sickLeaveRepository.save(sickLeave);
        return "redirect:/sick-leaves";
    }

    @GetMapping("/delete/{id}")
    public String deleteSickLeave(@PathVariable Long id) {
        sickLeaveRepository.deleteById(id);
        return "redirect:/sick-leaves";
    }

    @GetMapping("/edit/{id}")
    public String editSickLeave(@PathVariable Long id, Model model) {
        SickLeave sickLeave = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sick leave Id:" + id));
        model.addAttribute("sickLeave", sickLeave);
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        return "edit_sick_leave";
    }

    @PostMapping("/edit/{id}")
    public String updateSickLeave(@PathVariable Long id,
                                   @Valid @ModelAttribute("sickLeave") SickLeave sickLeave,
                                   BindingResult bindingResult,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("doctors", doctorRepository.findAll());
            return "edit_sick_leave";
        }
        SickLeave existing = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sick leave Id:" + id));
        Patient patient = patientRepository.findById(sickLeave.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient Id:" + sickLeave.getPatient().getId()));
        Doctor doctor = doctorRepository.findById(sickLeave.getDoctor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor Id:" + sickLeave.getDoctor().getId()));
        existing.setPatient(patient);
        existing.setDoctor(doctor);
        existing.setStartDate(sickLeave.getStartDate());
        existing.setDurationInDays(sickLeave.getDurationInDays());
        sickLeaveRepository.save(existing);
        return "redirect:/sick-leaves";
    }
}
