package com.medrecords.medicalrecords.repository;

import com.medrecords.medicalrecords.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(path = "visits")
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPatientId(Long patientId);
    List<Visit> findByDoctorId(Long doctorId);
    List<Visit> findByVisitDateBetween(LocalDate start, LocalDate end);
    List<Visit> findByDoctorIdAndVisitDateBetween(Long doctorId, LocalDate start, LocalDate end);
}
