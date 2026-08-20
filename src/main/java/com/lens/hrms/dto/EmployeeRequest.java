package com.lens.hrms.dto;

import jakarta.validation.constraints.*;

public class EmployeeRequest {
    @NotBlank(message="Name is required")
    @Size(max=120, message="Name must be at most 120 characters")
    private String name;

    @NotBlank(message="Email is required")
    @Email(message="Invalid email")
    private String email;

    @NotBlank(message="Department is required")
    private String department;

    @NotBlank(message="Designation is required")
    private String designation;

    @NotNull(message="Salary is required")
    @PositiveOrZero(message="Salary cannot be negative")
    private Double salary;

    @NotBlank(message="Status is required")
    private String status;

    public EmployeeRequest() {}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getDepartment(){return department;} public void setDepartment(String v){department=v;}
    public String getDesignation(){return designation;} public void setDesignation(String v){designation=v;}
    public Double getSalary(){return salary;} public void setSalary(Double v){salary=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
