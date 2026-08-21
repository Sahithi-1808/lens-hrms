package com.lens.hrms.exception;

public class DepartmentNotFoundException extends RuntimeException {

    public DepartmentNotFoundException(String name) {
        super("Department not found: " + name);
    }
}