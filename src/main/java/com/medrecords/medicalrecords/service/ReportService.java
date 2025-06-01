package com.medrecords.medicalrecords.service;

import com.medrecords.medicalrecords.repository.PatientRepository;
import com.medrecords.medicalrecords.repository.VisitRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;

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
}
