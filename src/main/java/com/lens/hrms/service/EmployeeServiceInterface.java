package com.lens.hrms.service;

import com.lens.hrms.dto.EmployeeRequest;
import com.lens.hrms.dto.EmployeeResponse;
import org.springframework.data.domain.Page;

public interface EmployeeServiceInterface {

    EmployeeResponse create(EmployeeRequest request);

    Page<EmployeeResponse> getAll(
            int page,
            int size,
            String sortBy,
            String direction,
            String search,
            String department,
            String designation,
            String status,
            Double minSalary,
            Double maxSalary
    );

    EmployeeResponse getById(Long id);

    EmployeeResponse update(Long id, EmployeeRequest request);

    void delete(Long id);
}