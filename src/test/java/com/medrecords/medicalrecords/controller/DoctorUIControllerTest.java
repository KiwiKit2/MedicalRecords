package com.medrecords.medicalrecords.controller;

import com.medrecords.medicalrecords.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class DoctorUIControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DoctorRepository doctorRepository;

    @Test
    void testDoctorsPage() throws Exception {
        MockitoAnnotations.openMocks(this);
        DoctorUIController doctorUIController = new DoctorUIController(doctorRepository);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(doctorUIController).setViewResolvers(viewResolver).build();

        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors"));
    }
}
