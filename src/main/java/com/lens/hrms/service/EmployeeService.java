package com.lens.hrms.service;

import com.lens.hrms.dto.EmployeeRequest;
import com.lens.hrms.dto.EmployeeResponse;
import com.lens.hrms.entity.Employee;
import com.lens.hrms.exception.*;
import com.lens.hrms.repository.EmployeeRepository;
import org.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) { this.repository = repository; }

    public EmployeeResponse create(EmployeeRequest request) {
        if (repository.existsByEmail(request.getEmail())) throw new DuplicateEmailException(request.getEmail());
        Employee e = new Employee();
        copy(request, e);
        Employee saved = repository.save(e);
        log.info("Created employee id={} email={}", saved.getId(), saved.getEmail());
        return toResponse(saved);
    }

    public Page<EmployeeResponse> getAll(int page, int size, String sortBy, String direction, String search) {
        String safeSort = switch (sortBy) {
            case "id","name","email","department","designation","salary","status" -> sortBy;
            default -> "id";
        };
        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(safeSort).descending() : Sort.by(safeSort).ascending();
        Pageable pageable = PageRequest.of(Math.max(page,0), Math.min(Math.max(size,1),100), sort);
        Page<Employee> result = (search == null || search.isBlank())
                ? repository.findAll(pageable)
                : repository.findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(search, search, pageable);
        return result.map(this::toResponse);
    }

    public EmployeeResponse getById(Long id) {
        return toResponse(find(id));
    }

    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee e = find(id);
        if (!e.getEmail().equalsIgnoreCase(request.getEmail())
                && repository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }
        copy(request, e);
        Employee saved = repository.save(e);
        log.info("Updated employee id={}", id);
        return toResponse(saved);
    }

    public void delete(Long id) {
        Employee e = find(id);
        repository.delete(e);
        log.info("Deleted employee id={}", id);
    }

    private Employee find(Long id) {
        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private void copy(EmployeeRequest r, Employee e) {
        e.setName(r.getName()); e.setEmail(r.getEmail()); e.setDepartment(r.getDepartment());
        e.setDesignation(r.getDesignation()); e.setSalary(r.getSalary()); e.setStatus(r.getStatus());
    }

    private EmployeeResponse toResponse(Employee e) {
        return new EmployeeResponse(e.getId(), e.getName(), e.getEmail(), e.getDepartment(),
                e.getDesignation(), e.getSalary(), e.getStatus());
    }
}
