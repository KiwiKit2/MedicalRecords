package com.medrecords.medicalrecords.service;

import com.medrecords.medicalrecords.model.Diagnosis;
import com.medrecords.medicalrecords.model.Doctor;
import com.medrecords.medicalrecords.model.Patient;
import com.medrecords.medicalrecords.model.Visit;
import com.medrecords.medicalrecords.repository.DiagnosisRepository;
import com.medrecords.medicalrecords.repository.DoctorRepository;
import com.medrecords.medicalrecords.repository.PatientRepository;
import com.medrecords.medicalrecords.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DiagnosisRepository diagnosisRepository;

    public ReportService(VisitRepository visitRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, DiagnosisRepository diagnosisRepository) {
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.diagnosisRepository = diagnosisRepository;
    }

    public Map<String, Long> getDiagnosisCounts() {
        return visitRepository.findAll().stream()
                .filter(visit -> visit.getDiagnosis() != null)
                .collect(Collectors.groupingBy(visit -> visit.getDiagnosis().getName(), Collectors.counting()));
    }

    public Map<String, Long> getPatientsPerDoctor() {
        return patientRepository.findAll().stream()
                .filter(patient -> patient.getPrimaryDoctor() != null)
                .collect(Collectors.groupingBy(patient -> patient.getPrimaryDoctor().getName(), Collectors.counting()));
    }

    public Map<String, Long> getVisitsPerDoctor() {
        return visitRepository.findAll().stream()
                .collect(Collectors.groupingBy(visit -> visit.getDoctor().getName(), Collectors.counting()));
    }
}
