package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.Patient;
import com.medrecords.medicalrecords.repository.DoctorRepository;
import com.medrecords.medicalrecords.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class RegistrationController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("doctors", doctorRepository.findAll());
        return "register";
    }

    @PostMapping("/register")
    public String registerPatient(
            @Valid @ModelAttribute("patient") Patient patient,
            BindingResult bindingResult,
            @RequestParam("doctorId") Long doctorId,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctors", doctorRepository.findAll());
            return "register";
        }
        // set selected doctor as primary
        doctorRepository.findById(doctorId).ifPresent(patient::setPrimaryDoctor);
        // capture raw password for login
        String rawPassword = patient.getPassword();
        // encode and set password
        patient.setPassword(passwordEncoder.encode(rawPassword));
        try {
            patientRepository.save(patient);
            // auto-login
            UsernamePasswordAuthenticationToken authReq =
                    new UsernamePasswordAuthenticationToken(patient.getEgn(), rawPassword);
            var auth = authenticationManager.authenticate(authReq);
            SecurityContextHolder.getContext().setAuthentication(auth);
            return "redirect:/my-visits";
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // handle unique EGN constraint violation
            bindingResult.rejectValue("egn", "error.patient", "EGN already registered");
            model.addAttribute("doctors", doctorRepository.findAll());
            return "register";
        }
    }
}
