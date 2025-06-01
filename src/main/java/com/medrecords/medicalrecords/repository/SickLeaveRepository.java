package com.medrecords.medicalrecords.repository;

import com.medrecords.medicalrecords.model.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(path = "sick-leaves")
public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {
    List<SickLeave> findByDoctorId(Long doctorId);
    List<SickLeave> findByStartDateBetween(LocalDate start, LocalDate end);
}
