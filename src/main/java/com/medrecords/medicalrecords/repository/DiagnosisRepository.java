package com.medrecords.medicalrecords.repository;

import com.medrecords.medicalrecords.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "diagnoses")
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
}
