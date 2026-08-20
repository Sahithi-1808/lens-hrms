package com.lens.hrms.repository;
import com.lens.hrms.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);
    Optional<Employee> findByEmail(String email);
    Page<Employee> findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
            String name, String department, Pageable pageable);
}
