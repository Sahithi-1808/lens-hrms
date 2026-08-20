package com.lens.hrms.repository;

import com.lens.hrms.entity.Employee;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryEmployeeRepository {

    private final Map<Long, Employee> employees = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Employee save(Employee employee) {
        if (employee.getId() == null) {
            employee.setId(idGenerator.incrementAndGet());
        }

        employees.put(employee.getId(), employee);
        return employee;
    }

    public List<Employee> findAll() {
        return new ArrayList<>(employees.values());
    }

    public Employee findById(Long id) {
        return employees.get(id);
    }

    public boolean existsByEmail(String email) {
        return employees.values().stream()
                .anyMatch(employee ->
                        employee.getEmail().equalsIgnoreCase(email));
    }

    public void delete(Long id) {
        employees.remove(id);
    }

    public long count() {
        return employees.size();
    }
}