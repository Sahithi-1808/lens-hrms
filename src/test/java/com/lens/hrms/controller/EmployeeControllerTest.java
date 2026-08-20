package com.lens.hrms.controller;

import com.lens.hrms.dto.EmployeeRequest;
import com.lens.hrms.dto.EmployeeResponse;
import com.lens.hrms.service.EmployeeServiceInterface;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(EmployeeControllerTest.TestSecurityConfig.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeServiceInterface service;


    // ============================================================
    // TEST SECURITY CONFIGURATION
    // ============================================================

    @TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http)
                throws Exception {

            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                    .anyRequest().authenticated()
                );

            return http.build();
        }
    }


    // ============================================================
    // GET
    // ============================================================

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void employeeCanGetById() throws Exception {

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


    // ============================================================
    // CREATE
    // ============================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateEmployee() throws Exception {

        when(service.create(any(EmployeeRequest.class))).thenReturn(
            new EmployeeResponse(
                1L,
                "Alice",
                "alice@test.com",
                "IT",
                "Developer",
                50000.0,
                "ACTIVE"
            )
        );

        mockMvc.perform(
                post("/api/employees")
                    .contentType("application/json")
                    .content("""
                        {
                          "name": "Alice",
                          "email": "alice@test.com",
                          "department": "IT",
                          "designation": "Developer",
                          "salary": 50000,
                          "status": "ACTIVE"
                        }
                    """)
            )
            .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser(roles = "HR")
    void hrCanCreateEmployee() throws Exception {

        when(service.create(any(EmployeeRequest.class))).thenReturn(
            new EmployeeResponse(
                1L,
                "Alice",
                "alice@test.com",
                "IT",
                "Developer",
                50000.0,
                "ACTIVE"
            )
        );

        mockMvc.perform(
                post("/api/employees")
                    .contentType("application/json")
                    .content("""
                        {
                          "name": "Alice",
                          "email": "alice@test.com",
                          "department": "IT",
                          "designation": "Developer",
                          "salary": 50000,
                          "status": "ACTIVE"
                        }
                    """)
            )
            .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void employeeCannotCreateEmployee() throws Exception {

        mockMvc.perform(
                post("/api/employees")
                    .contentType("application/json")
                    .content("""
                        {
                          "name": "Alice",
                          "email": "alice@test.com",
                          "department": "IT",
                          "designation": "Developer",
                          "salary": 50000,
                          "status": "ACTIVE"
                        }
                    """)
            )
            .andExpect(status().isForbidden());
    }


    // ============================================================
    // UPDATE
    // ============================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanUpdateEmployee() throws Exception {

        when(service.update(
                eq(1L),
                any(EmployeeRequest.class)
        )).thenReturn(
            new EmployeeResponse(
                1L,
                "Alice Updated",
                "alice@test.com",
                "IT",
                "Senior Developer",
                60000.0,
                "ACTIVE"
            )
        );

        mockMvc.perform(
                put("/api/employees/1")
                    .contentType("application/json")
                    .content("""
                        {
                          "name": "Alice Updated",
                          "email": "alice@test.com",
                          "department": "IT",
                          "designation": "Senior Developer",
                          "salary": 60000,
                          "status": "ACTIVE"
                        }
                    """)
            )
            .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void employeeCannotUpdateEmployee() throws Exception {

        mockMvc.perform(
                put("/api/employees/1")
                    .contentType("application/json")
                    .content("""
                        {
                          "name": "Alice Updated",
                          "email": "alice@test.com",
                          "department": "IT",
                          "designation": "Developer",
                          "salary": 50000,
                          "status": "ACTIVE"
                        }
                    """)
            )
            .andExpect(status().isForbidden());
    }


    // ============================================================
    // DELETE
    // ============================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanDeleteEmployee() throws Exception {

        mockMvc.perform(
                delete("/api/employees/1")
            )
            .andExpect(status().isNoContent());
    }


    @Test
    @WithMockUser(roles = "HR")
    void hrCannotDeleteEmployee() throws Exception {

        mockMvc.perform(
                delete("/api/employees/1")
            )
            .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void employeeCannotDeleteEmployee() throws Exception {

        mockMvc.perform(
                delete("/api/employees/1")
            )
            .andExpect(status().isForbidden());
    }
}