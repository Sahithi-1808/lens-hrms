-- Run once against an existing Lens HRMS database created by an older version.
-- The current Employee entity stores the relationship in department_id.
ALTER TABLE employees MODIFY department VARCHAR(100) NULL;

-- Optional verification
SELECT id, name, department_id, department FROM employees ORDER BY id;
