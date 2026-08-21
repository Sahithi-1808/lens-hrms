package com.lens.hrms.service;

import com.lens.hrms.dto.EmployeeRequest;
import com.lens.hrms.dto.EmployeeResponse;
import com.lens.hrms.entity.Department;
import com.lens.hrms.entity.Employee;
import com.lens.hrms.exception.DuplicateEmailException;
import com.lens.hrms.exception.EmployeeNotFoundException;
import com.lens.hrms.exception.DepartmentNotFoundException;
import com.lens.hrms.repository.DepartmentRepository;
import com.lens.hrms.repository.EmployeeRepository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService implements EmployeeServiceInterface {

    private static final Logger log =
            LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository repository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(
            EmployeeRepository repository,
            DepartmentRepository departmentRepository) {

        this.repository = repository;
        this.departmentRepository = departmentRepository;
    }

    // ============================================================
    // CREATE
    // ============================================================

    @Override
    public EmployeeResponse create(EmployeeRequest request) {

        if (repository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Employee employee = new Employee();

        copy(request, employee);

        Employee saved = repository.save(employee);

        log.info(
                "Created employee id={} email={}",
                saved.getId(),
                saved.getEmail()
        );

        return toResponse(saved);
    }

    // ============================================================
    // ADVANCED SEARCH + FILTER + SORT + PAGINATION
    // ============================================================

    @Override
    public Page<EmployeeResponse> getAll(
            int page,
            int size,
            String sortBy,
            String direction,
            String search,
            String department,
            String designation,
            String status,
            Double minSalary,
            Double maxSalary) {

        // --------------------------------------------------------
        // Safe sorting
        // --------------------------------------------------------

        String safeSort = switch (sortBy) {

            case "id",
                 "name",
                 "email",
                 "department",
                 "designation",
                 "salary",
                 "status" -> sortBy;

            default -> "id";
        };

        // Department is now a relationship.
        // Sorting by department should use department.name.
        Sort sort;

        if ("department".equals(safeSort)) {
            sort = "desc".equalsIgnoreCase(direction)
                    ? Sort.by("department.name").descending()
                    : Sort.by("department.name").ascending();
        } else {
            sort = "desc".equalsIgnoreCase(direction)
                    ? Sort.by(safeSort).descending()
                    : Sort.by(safeSort).ascending();
        }

        // --------------------------------------------------------
        // Safe pagination
        // --------------------------------------------------------

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                sort
        );

        // --------------------------------------------------------
        // Dynamic Specification
        // --------------------------------------------------------

        Specification<Employee> specification =
                Specification.where(null);

        // --------------------------------------------------------
        // General search
        // Searches:
        // name
        // email
        // department name
        // designation
        // --------------------------------------------------------

        if (search != null && !search.isBlank()) {

            String searchValue = search.trim().toLowerCase();

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.or(

                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get("name")
                                            ),
                                            "%" + searchValue + "%"
                                    ),

                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get("email")
                                            ),
                                            "%" + searchValue + "%"
                                    ),

                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get("department")
                                                         .get("name")
                                            ),
                                            "%" + searchValue + "%"
                                    ),

                                    criteriaBuilder.like(
                                            criteriaBuilder.lower(
                                                    root.get("designation")
                                            ),
                                            "%" + searchValue + "%"
                                    )
                            )
            );
        }

        // --------------------------------------------------------
        // Department filter
        // --------------------------------------------------------

        if (department != null && !department.isBlank()) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    criteriaBuilder.lower(
                                            root.get("department")
                                                .get("name")
                                    ),
                                    department.trim().toLowerCase()
                            )
            );
        }

        // --------------------------------------------------------
        // Designation filter
        // --------------------------------------------------------

        if (designation != null && !designation.isBlank()) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    criteriaBuilder.lower(
                                            root.get("designation")
                                    ),
                                    designation.trim().toLowerCase()
                            )
            );
        }

        // --------------------------------------------------------
        // Status filter
        // --------------------------------------------------------

        if (status != null && !status.isBlank()) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    criteriaBuilder.lower(
                                            root.get("status")
                                    ),
                                    status.trim().toLowerCase()
                            )
            );
        }

        // --------------------------------------------------------
        // Minimum salary
        // --------------------------------------------------------

        if (minSalary != null) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(
                                    root.get("salary"),
                                    minSalary
                            )
            );
        }

        // --------------------------------------------------------
        // Maximum salary
        // --------------------------------------------------------

        if (maxSalary != null) {

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.lessThanOrEqualTo(
                                    root.get("salary"),
                                    maxSalary
                            )
            );
        }

        // --------------------------------------------------------
        // Execute query
        // --------------------------------------------------------

        Page<Employee> result =
                repository.findAll(specification, pageable);

        log.info(
                "Employee search executed page={} size={} search={} department={} designation={} status={}",
                page,
                size,
                search,
                department,
                designation,
                status
        );

        return result.map(this::toResponse);
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @Override
    public EmployeeResponse getById(Long id) {
        return toResponse(find(id));
    }

    // ============================================================
    // UPDATE
    // ============================================================

    @Override
    public EmployeeResponse update(
            Long id,
            EmployeeRequest request) {

        Employee employee = find(id);

        if (!employee.getEmail().equalsIgnoreCase(request.getEmail())
                && repository.existsByEmail(request.getEmail())) {

            throw new DuplicateEmailException(request.getEmail());
        }

        copy(request, employee);

        Employee saved = repository.save(employee);

        log.info("Updated employee id={}", id);

        return toResponse(saved);
    }

    // ============================================================
    // DELETE
    // ============================================================

    @Override
    public void delete(Long id) {

        Employee employee = find(id);

        repository.delete(employee);

        log.info("Deleted employee id={}", id);
    }

    // ============================================================
    // FIND
    // ============================================================

    private Employee find(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(id));
    }

    // ============================================================
    // COPY REQUEST -> ENTITY
    // ============================================================

    private void copy(
            EmployeeRequest request,
            Employee employee) {

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());

        Department department = departmentRepository
                .findByNameIgnoreCase(request.getDepartment().trim())
                .orElseThrow(() ->
                        new DepartmentNotFoundException(
                        request.getDepartment()
                        ));

        employee.setDepartment(department);

        employee.setDesignation(request.getDesignation());
        employee.setSalary(request.getSalary());
        employee.setStatus(request.getStatus());
    }

    // ============================================================
    // ENTITY -> RESPONSE
    // ============================================================

    private EmployeeResponse toResponse(Employee employee) {

        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment().getName(),
                employee.getDesignation(),
                employee.getSalary(),
                employee.getStatus()
        );
    }
}