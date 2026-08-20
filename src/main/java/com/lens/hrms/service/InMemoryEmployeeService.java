package com.lens.hrms.service;

import com.lens.hrms.dto.EmployeeRequest;
import com.lens.hrms.dto.EmployeeResponse;
import com.lens.hrms.entity.Employee;
import com.lens.hrms.exception.DuplicateEmailException;
import com.lens.hrms.exception.EmployeeNotFoundException;
import com.lens.hrms.repository.InMemoryEmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InMemoryEmployeeService {

    private final InMemoryEmployeeRepository repository;

    public InMemoryEmployeeService(InMemoryEmployeeRepository repository) {
        this.repository = repository;
    }

    public EmployeeResponse create(EmployeeRequest request) {

        if (repository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Employee employee = new Employee();
        copy(request, employee);

        Employee saved = repository.save(employee);

        return toResponse(saved);
    }

    public List<EmployeeResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EmployeeResponse getById(Long id) {

        return toResponse(find(id));
    }

    public EmployeeResponse update(Long id, EmployeeRequest request) {

        Employee employee = find(id);

        if (!employee.getEmail().equalsIgnoreCase(request.getEmail())
                && repository.existsByEmail(request.getEmail())) {

            throw new DuplicateEmailException(request.getEmail());
        }

        copy(request, employee);

        Employee updated = repository.save(employee);

        return toResponse(updated);
    }

    public void delete(Long id) {

        find(id);

        repository.delete(id);
    }

    private Employee find(Long id) {

        Employee employee = repository.findById(id);

        if (employee == null) {
            throw new EmployeeNotFoundException(id);
        }

        return employee;
    }

    private void copy(EmployeeRequest request, Employee employee) {

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setSalary(request.getSalary());
        employee.setStatus(request.getStatus());
    }

    private EmployeeResponse toResponse(Employee employee) {

        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getSalary(),
                employee.getStatus()
        );
    }
}