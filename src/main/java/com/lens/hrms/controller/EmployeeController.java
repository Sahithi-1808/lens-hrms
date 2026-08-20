package com.lens.hrms.controller;

import com.lens.hrms.dto.ApiResponse;
import com.lens.hrms.dto.EmployeeRequest;
import com.lens.hrms.dto.EmployeeResponse;
import com.lens.hrms.service.EmployeeServiceInterface;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeServiceInterface service;

    public EmployeeController(EmployeeServiceInterface service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(
            @Valid @RequestBody EmployeeRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Employee created successfully",
                        service.create(request)
                ));
    }

    @GetMapping
    public ApiResponse<Page<EmployeeResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search) {

        return new ApiResponse<>(
                true,
                "Employees fetched successfully",
                service.getAll(page, size, sortBy, direction, search)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<EmployeeResponse> getById(
            @PathVariable Long id) {

        return new ApiResponse<>(
                true,
                "Employee fetched successfully",
                service.getById(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ApiResponse<EmployeeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {

        return new ApiResponse<>(
                true,
                "Employee updated successfully",
                service.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}