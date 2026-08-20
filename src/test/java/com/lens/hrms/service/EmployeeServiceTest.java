package com.lens.hrms.service;

import com.lens.hrms.dto.EmployeeRequest;
import com.lens.hrms.entity.Employee;
import com.lens.hrms.exception.EmployeeNotFoundException;
import com.lens.hrms.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeServiceTest {

    @Mock
    EmployeeRepository repository;

    @InjectMocks
    EmployeeService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getByIdReturnsEmployee() {
        Employee e = new Employee();
        e.setId(1L);
        e.setName("Alice");
        e.setEmail("alice@test.com");

        when(repository.findById(1L)).thenReturn(Optional.of(e));

        var result = service.getById(1L);

        assertEquals("Alice", result.name());
        verify(repository).findById(1L);
    }

    @Test
    void getByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> service.getById(99L)
        );
    }

    @Test
    void createSavesEmployee() {
        EmployeeRequest r = new EmployeeRequest();

        r.setName("Bob");
        r.setEmail("bob@test.com");
        r.setDepartment("IT");
        r.setDesignation("Developer");
        r.setSalary(50000.0);
        r.setStatus("ACTIVE");

        when(repository.existsByEmail(r.getEmail())).thenReturn(false);

        when(repository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee e = invocation.getArgument(0);
            e.setId(10L);
            return e;
        });

        var result = service.create(r);

        assertEquals(10L, result.id());
        assertEquals("Bob", result.name());

        verify(repository).save(any(Employee.class));
    }
}