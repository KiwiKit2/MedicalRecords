package com.medrecords.medicalrecords.repository;

import com.medrecords.medicalrecords.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "patients")
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByPrimaryDoctorId(Long doctorId);
    List<Patient> findByEgn(String egn);
}
