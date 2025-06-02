package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.model.Visit;
import com.medrecords.medicalrecords.repository.PatientRepository;
import com.medrecords.medicalrecords.repository.VisitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MyVisitsController.class)
class MyVisitsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitRepository visitRepository;

    @MockBean
    private PatientRepository patientRepository;

    @Test
    @WithMockUser(username = "patient", roles = {"PATIENT"})
    void myVisitsShouldShowVisitsForAuthenticatedPatient() throws Exception {
        // given
        var patientList = List.of(new com.medrecords.medicalrecords.model.Patient(1L, "John Doe", "1234567890", false, null));
        when(patientRepository.findByEgn("patient")).thenReturn(patientList);
        var visits = List.of(new Visit(1L, patientList.get(0), null, LocalDate.now(), null, "Treatment"));
        when(visitRepository.findByPatientId(1L)).thenReturn(visits);

        // when & then
        mockMvc.perform(get("/my-visits"))
                .andExpect(status().isOk())
                .andExpect(view().name("my_visits"))
                .andExpect(model().attributeExists("visits"));
    }

    @Test
    @WithMockUser(username = "other", roles = {"DOCTOR"})
    void myVisitsAccessDeniedForNonPatient() throws Exception {
        mockMvc.perform(get("/my-visits"))
                .andExpect(status().isForbidden());
    }
}
