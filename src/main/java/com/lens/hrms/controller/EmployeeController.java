package com.lens.hrms.controller;

import com.lens.hrms.dto.ApiResponse;
import com.lens.hrms.dto.EmployeeRequest;
import com.lens.hrms.dto.EmployeeResponse;
import com.lens.hrms.service.EmployeeServiceInterface;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    // ============================================================
    // CREATE
    // ============================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @Operation(
            summary = "Create employee",
            description = "Creates a new employee. Accessible to ADMIN and HR."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Employee created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation failed"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Department not found"
            ),
           @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email already exists"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
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

    // ============================================================
    // ADVANCED SEARCH
    // ============================================================

    @GetMapping
    @Operation(
            summary = "Search employees",
            description = """
                    Retrieves employees with optional search and filtering.

                    Supported filters:
                    - search: searches name, email, department and designation
                    - department: exact department filter
                    - designation: exact designation filter
                    - status: employee status filter
                    - minSalary: minimum salary
                    - maxSalary: maximum salary
                    - page: page number starting from 0
                    - size: number of records per page
                    - sortBy: field used for sorting
                    - direction: asc or desc
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employees fetched successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    public ApiResponse<Page<EmployeeResponse>> getAll(

            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(description = "Number of records per page, maximum 100")
            @RequestParam(defaultValue = "10")
            int size,

            @Parameter(
                    description = "Sort field: id, name, email, department, designation, salary, status"
            )
            @RequestParam(defaultValue = "id")
            String sortBy,

            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "asc")
            String direction,

            @Parameter(
                    description = "Search name, email, department or designation"
            )
            @RequestParam(required = false)
            String search,

            @Parameter(description = "Filter by department")
            @RequestParam(required = false)
            String department,

            @Parameter(description = "Filter by designation")
            @RequestParam(required = false)
            String designation,

            @Parameter(description = "Filter by employee status")
            @RequestParam(required = false)
            String status,

            @Parameter(description = "Minimum salary")
            @RequestParam(required = false)
            Double minSalary,

            @Parameter(description = "Maximum salary")
            @RequestParam(required = false)
            Double maxSalary) {

        return new ApiResponse<>(
                true,
                "Employees fetched successfully",
                service.getAll(
                        page,
                        size,
                        sortBy,
                        direction,
                        search,
                        department,
                        designation,
                        status,
                        minSalary,
                        maxSalary
                )
        );
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @GetMapping("/{id}")
    @Operation(
            summary = "Get employee by ID",
            description = "Fetches a single employee using the employee ID."
    )
    @ApiResponses({
           @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee fetched successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Employee not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    public ApiResponse<EmployeeResponse> getById(
            @PathVariable Long id) {

        return new ApiResponse<>(
                true,
                "Employee fetched successfully",
                service.getById(id)
        );
    }

    // ============================================================
    // UPDATE
    // ============================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @Operation(
            summary = "Update employee",
            description = "Updates an existing employee. Accessible to ADMIN and HR."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employee updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation failed"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Employee or department not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email already exists"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    public ApiResponse<EmployeeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {

        return new ApiResponse<>(
                true,
                "Employee updated successfully",
                service.update(id, request)
        );
    }

    // ============================================================
    // DELETE
    // ============================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete employee",
            description = "Deletes an employee. Accessible only to ADMIN."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "Employee deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Employee not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}