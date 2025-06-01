package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.Diagnosis;
import com.medrecords.medicalrecords.repository.DiagnosisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/diagnoses")
public class DiagnosisUIController {

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @GetMapping
    public String listDiagnoses(Model model) {
        model.addAttribute("diagnoses", diagnosisRepository.findAll());
        return "diagnoses";
    }

    @PostMapping
    public String addDiagnosis(@RequestParam String name, @RequestParam String description) {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setName(name);
        diagnosis.setDescription(description);
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
    public String updateDiagnosis(@PathVariable Long id, @RequestParam String name, @RequestParam String description) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid diagnosis Id:" + id));
        diagnosis.setName(name);
        diagnosis.setDescription(description);
        diagnosisRepository.save(diagnosis);
        return "redirect:/diagnoses";
    }
}
