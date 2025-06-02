package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.Doctor;
import com.medrecords.medicalrecords.repository.DoctorRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctors")
public class DoctorUIController {

    private final DoctorRepository doctorRepository;

    public DoctorUIController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public String listDoctors(Model model) {
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("doctor", new Doctor());
        return "doctors";
    }

    @PostMapping
    public String addDoctor(@Valid @ModelAttribute("doctor") Doctor doctor,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctors", doctorRepository.findAll());
            return "doctors";
        }
        doctorRepository.save(doctor);
        return "redirect:/doctors";
    }

    @GetMapping("/delete/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        doctorRepository.deleteById(id);
        return "redirect:/doctors";
    }

    @GetMapping("/edit/{id}")
    public String editDoctor(@PathVariable Long id, Model model) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid doctor Id:" + id));
        model.addAttribute("doctor", existing);
        return "edit_doctor";
    }

    @PostMapping("/edit/{id}")
    public String updateDoctor(@PathVariable Long id,
                               @Valid @ModelAttribute("doctor") Doctor doctor,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctor", doctor);
            return "edit_doctor";
        }
        doctor.setId(id);
        doctorRepository.save(doctor);
        return "redirect:/doctors";
    }
}
