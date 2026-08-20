package com.lens.hrms.dto;

public record ApiResponse<T>(boolean success, String message, T data) {}
