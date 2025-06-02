package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.Visit;
import com.medrecords.medicalrecords.repository.PatientRepository;
import com.medrecords.medicalrecords.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/my-visits")
public class MyVisitsController {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping
    public String myVisits(Model model, Authentication authentication) {
        var patient = patientRepository.findByEgn(authentication.getName());
        if (patient.isEmpty()) {
            model.addAttribute("visits", List.of());
        } else {
            model.addAttribute("visits", visitRepository.findByPatientId(patient.get(0).getId()));
        }
        return "my_visits";
    }
}
