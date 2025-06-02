package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.SickLeave;
import com.medrecords.medicalrecords.model.Patient;
import com.medrecords.medicalrecords.model.Doctor;
import com.medrecords.medicalrecords.repository.SickLeaveRepository;
import com.medrecords.medicalrecords.repository.PatientRepository;
import com.medrecords.medicalrecords.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SickLeaveUIController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class SickLeaveUIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SickLeaveRepository sickLeaveRepository;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private DoctorRepository doctorRepository;

    @Test
    void listShouldPopulateModelAndRenderView() throws Exception {
        when(sickLeaveRepository.findAll()).thenReturn(List.of());
        when(patientRepository.findAll()).thenReturn(List.of());
        when(doctorRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/sick-leaves"))
                .andExpect(status().isOk())
                .andExpect(view().name("sick_leaves"))
                .andExpect(model().attributeExists("sickLeaves", "patients", "doctors", "sickLeave"));
    }

    @Test
    void editFormShouldShowSickLeave() throws Exception {
        var patient = new Patient(1L, "John", "1234567890", false, null);
        var doctor = new Doctor(1L, "Dr A", null, null);
        var sl = new SickLeave(1L, patient, LocalDate.now(), LocalDate.now().plusDays(5), 5, doctor);
        when(sickLeaveRepository.findById(1L)).thenReturn(Optional.of(sl));
        when(patientRepository.findAll()).thenReturn(List.of(patient));
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));

        mockMvc.perform(get("/sick-leaves/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit_sick_leave"))
                .andExpect(model().attributeExists("sickLeave", "patients", "doctors"));
    }
}
