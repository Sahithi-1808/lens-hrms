package com.lens.hrms.controller;

import com.lens.hrms.dto.EmployeeResponse;
import com.lens.hrms.security.JwtService;
import com.lens.hrms.service.EmployeeService;
import com.lens.hrms.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void getByIdReturnsOk() throws Exception {

        when(service.getById(1L)).thenReturn(
            new EmployeeResponse(
                1L,
                "Alice",
                "alice@test.com",
                "HR",
                "Manager",
                50000.0,
                "ACTIVE"
            )
        );

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk());
    }
}