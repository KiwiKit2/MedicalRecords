package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.Diagnosis;
import com.medrecords.medicalrecords.repository.DiagnosisRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/diagnoses")
public class DiagnosisUIController {

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @GetMapping
    public String listDiagnoses(Model model) {
        model.addAttribute("diagnoses", diagnosisRepository.findAll());
        model.addAttribute("diagnosis", new Diagnosis());
        return "diagnoses";
    }

    @PostMapping
    public String addDiagnosis(@Valid @ModelAttribute("diagnosis") Diagnosis diagnosis,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("diagnoses", diagnosisRepository.findAll());
            return "diagnoses";
        }
        diagnosisRepository.save(diagnosis);
        return "redirect:/diagnoses";
    }

    @GetMapping("/delete/{id}")
    public String deleteDiagnosis(@PathVariable Long id) {
        diagnosisRepository.deleteById(id);
        return "redirect:/diagnoses";
    }

    @GetMapping("/edit/{id}")
    public String editDiagnosis(@PathVariable Long id, Model model) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid diagnosis Id:" + id));
        model.addAttribute("diagnosis", diagnosis);
        return "edit_diagnosis";
    }

    @PostMapping("/edit/{id}")
    public String updateDiagnosis(@PathVariable Long id,
                                  @Valid @ModelAttribute("diagnosis") Diagnosis diagnosis,
                                  BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            return "edit_diagnosis";
        }
        diagnosis.setId(id);
        diagnosisRepository.save(diagnosis);
        return "redirect:/diagnoses";
    }
}
