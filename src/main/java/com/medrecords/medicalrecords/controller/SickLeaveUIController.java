package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.*;
import com.medrecords.medicalrecords.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        return "sick_leaves";
    }

    @PostMapping
    public String addSickLeave(@RequestParam Long patientId, @RequestParam Long doctorId,
                                @RequestParam String startDate, @RequestParam int durationInDays) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient Id:" + patientId));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor Id:" + doctorId));

        SickLeave sickLeave = new SickLeave();
        sickLeave.setPatient(patient);
        sickLeave.setDoctor(doctor);
        sickLeave.setStartDate(LocalDate.parse(startDate));
        sickLeave.setDurationInDays(durationInDays);
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
    public String updateSickLeave(@PathVariable Long id, @RequestParam Long patientId, @RequestParam Long doctorId,
                                   @RequestParam String startDate, @RequestParam int durationInDays) {
        SickLeave sickLeave = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sick leave Id:" + id));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient Id:" + patientId));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor Id:" + doctorId));

        sickLeave.setPatient(patient);
        sickLeave.setDoctor(doctor);
        sickLeave.setStartDate(LocalDate.parse(startDate));
        sickLeave.setDurationInDays(durationInDays);
        sickLeaveRepository.save(sickLeave);
        return "redirect:/sick-leaves";
    }
}
