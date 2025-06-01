package com.medrecords.medicalrecords.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN")
                .build();
        UserDetails doctor = User.withUsername("doctor")
                .password(passwordEncoder.encode("doctor"))
                .roles("DOCTOR")
                .build();
        UserDetails patient = User.withUsername("patient")
                .password(passwordEncoder.encode("patient"))
                .roles("PATIENT")
                .build();
        return new InMemoryUserDetailsManager(admin, doctor, patient);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .headers().frameOptions().disable() // allow H2 console
            .and()
            .authorizeHttpRequests()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            .and()
            .httpBasic();
        return http.build();
    }
}
