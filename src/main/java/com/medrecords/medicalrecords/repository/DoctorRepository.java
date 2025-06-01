package com.medrecords.medicalrecords.repository;

import com.medrecords.medicalrecords.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "doctors")
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
