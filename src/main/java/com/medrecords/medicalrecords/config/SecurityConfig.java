package com.medrecords.medicalrecords.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.medrecords.medicalrecords.model.Patient;
import com.medrecords.medicalrecords.repository.PatientRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder, PatientRepository patientRepository) {
        // In-memory users for ADMIN and DOCTOR
        InMemoryUserDetailsManager inMemory = new InMemoryUserDetailsManager(
            User.withUsername("admin").password(passwordEncoder.encode("admin")).roles("ADMIN").build(),
            User.withUsername("doctor").password(passwordEncoder.encode("doctor")).roles("DOCTOR").build()
        );
        return username -> {
            // try in-memory users first
            if (inMemory.userExists(username)) {
                return inMemory.loadUserByUsername(username);
            }
            // fallback to patient repository
            Patient patient = patientRepository.findByEgn(username)
                    .stream().findFirst()
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            return User.withUsername(patient.getEgn())
                    .password(patient.getPassword())
                    .roles("PATIENT")
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .userDetailsService(userDetailsService)
                   .passwordEncoder(passwordEncoder)
                   .and()
                   .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // allow H2 console
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/register", "/register/**", "/login", "/logout").permitAll()
                .requestMatchers("/api/**").authenticated()
                .requestMatchers("/my-visits/**").hasRole("PATIENT")
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }
}
