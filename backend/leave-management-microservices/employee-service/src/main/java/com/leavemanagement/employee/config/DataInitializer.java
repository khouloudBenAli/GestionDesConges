package com.leavemanagement.employee.config;

import com.leavemanagement.employee.entity.Department;
import com.leavemanagement.employee.entity.Employee;
import com.leavemanagement.employee.repository.DepartmentRepository;
import com.leavemanagement.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) {
        Department direction = departmentRepository.findByName("Direction").orElse(null);
        if (direction == null) {
            direction = departmentRepository.save(
                    Department.builder()
                            .name("Direction")
                            .description("Direction generale")
                            .build());
        }

        createEmployeeIfMissing("admin@leavemanagement.com", "Admin", "System", "Administrateur", direction);
        createEmployeeIfMissing("manager@leavemanagement.com", "Manager", "System", "Manager", direction);
        createEmployeeIfMissing("employee@leavemanagement.com", "Employee", "System", "Employe", direction);
    }

    private void createEmployeeIfMissing(String email, String firstName, String lastName, String jobTitle, Department department) {
        if (employeeRepository.existsByEmail(email)) {
            return;
        }
        employeeRepository.save(Employee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .jobTitle(jobTitle)
                .hireDate(LocalDate.now())
                .department(department)
                .build());
    }
}
