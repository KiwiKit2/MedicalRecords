package com.medrecords.medicalrecords.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping("/")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }
}
