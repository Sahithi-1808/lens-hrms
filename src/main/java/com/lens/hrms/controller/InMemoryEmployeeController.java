package com.lens.hrms.controller;

import com.lens.hrms.dto.ApiResponse;
import com.lens.hrms.dto.EmployeeRequest;
import com.lens.hrms.dto.EmployeeResponse;
import com.lens.hrms.service.InMemoryEmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/in-memory/employees")
public class InMemoryEmployeeController {

    private final InMemoryEmployeeService service;

    public InMemoryEmployeeController(InMemoryEmployeeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(
            @Valid @RequestBody EmployeeRequest request) {

        EmployeeResponse employee = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Employee created successfully in memory",
                        employee
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAll() {

        List<EmployeeResponse> employees = service.getAll();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Employees retrieved successfully from memory",
                        employees
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(
            @PathVariable Long id) {

        EmployeeResponse employee = service.getById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Employee retrieved successfully from memory",
                        employee
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {

        EmployeeResponse employee = service.update(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Employee updated successfully in memory",
                        employee
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}