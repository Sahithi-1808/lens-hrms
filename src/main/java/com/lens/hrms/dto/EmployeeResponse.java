package com.lens.hrms.dto;

public record EmployeeResponse(
    Long id, String name, String email, String department,
    String designation, Double salary, String status
) {}
