package com.medrecords.medicalrecords.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Map login page
        registry.addViewController("/login").setViewName("login");
        // Map root and dashboard to dashboard view
        registry.addViewController("/").setViewName("dashboard");
        registry.addViewController("/dashboard").setViewName("dashboard");
    }
}
