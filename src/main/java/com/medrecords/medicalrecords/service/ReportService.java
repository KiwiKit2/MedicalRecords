package com.medrecords.medicalrecords.service;

import com.medrecords.medicalrecords.model.Doctor;
import com.medrecords.medicalrecords.model.SickLeave;
import com.medrecords.medicalrecords.model.Visit;
import com.medrecords.medicalrecords.repository.PatientRepository;
import com.medrecords.medicalrecords.repository.SickLeaveRepository;
import com.medrecords.medicalrecords.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    @Autowired
    private SickLeaveRepository sickLeaveRepository;

    public ReportService(VisitRepository visitRepository, PatientRepository patientRepository) {
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
    }

    public Map<String, Long> getDiagnosisCounts() {
        try {
            return visitRepository.findAll().stream()
                    .filter(visit -> visit.getDiagnosis() != null)
                    .collect(Collectors.groupingBy(visit -> visit.getDiagnosis().getName(), Collectors.counting()));
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching diagnosis counts", e);
        }
    }

    public Map<String, Long> getPatientsPerDoctor() {
        try {
            return patientRepository.findAll().stream()
                    .filter(patient -> patient.getPrimaryDoctor() != null)
                    .collect(Collectors.groupingBy(patient -> patient.getPrimaryDoctor().getName(), Collectors.counting()));
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching patients per doctor", e);
        }
    }

    public Map<String, Long> getVisitsPerDoctor() {
        try {
            return visitRepository.findAll().stream()
                    .collect(Collectors.groupingBy(visit -> visit.getDoctor().getName(), Collectors.counting()));
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching visits per doctor", e);
        }
    }

    public List<Visit> getVisitsForPatient(Long patientId) {
        return visitRepository.findByPatientId(patientId);
    }

    public List<Visit> getVisitsForPeriod(LocalDate startDate, LocalDate endDate) {
        return visitRepository.findByVisitDateBetween(startDate, endDate);
    }

    public List<Visit> getVisitsForDoctorInPeriod(Long doctorId, LocalDate startDate, LocalDate endDate) {
        return visitRepository.findByDoctorIdAndVisitDateBetween(doctorId, startDate, endDate);
    }

    public String getMonthWithMostSickLeaves() {
        return sickLeaveRepository.findAll().stream()
                .collect(Collectors.groupingBy(sickLeave -> sickLeave.getStartDate().getMonth(), Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey().toString()).orElse("No data");
    }

    public List<Doctor> getDoctorsWithMostSickLeaves() {
        long maxCount = sickLeaveRepository.findAll().stream()
                .collect(Collectors.groupingBy(SickLeave::getDoctor, Collectors.counting()))
                .values().stream().max(Long::compare).orElse(0L);

        return sickLeaveRepository.findAll().stream()
                .collect(Collectors.groupingBy(SickLeave::getDoctor, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .distinct()
                .collect(Collectors.toList());
    }
}
