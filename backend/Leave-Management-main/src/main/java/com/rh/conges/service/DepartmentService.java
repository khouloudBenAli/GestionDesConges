package com.rh.conges.service;

import com.rh.conges.model.Department;
import com.rh.conges.model.Employee;
import com.rh.conges.repository.DepartmentRepository;
import com.rh.conges.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing departments
 */
@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Retrieves all departments
     * @return List of all departments
     */
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    /**
     * Retrieves a department by ID
     * @param id Department ID
     * @return The department if found
     * @throws RuntimeException if department not found
     */
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + id));
    }

    /**
     * Retrieves a department by name
     * @param name Department name
     * @return The department if found
     * @throws RuntimeException if department not found
     */
    public Department getDepartmentByName(String name) {
        return departmentRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Department not found with name: " + name));
    }

    /**
     * Checks if a department exists by name
     * @param name Department name
     * @return true if department exists, false otherwise
     */
    public boolean existsByName(String name) {
        return departmentRepository.existsByName(name);
    }

    /**
     * Saves a department
     * @param department Department to save
     * @return The saved department
     */
    @Transactional
    public Department saveDepartment(Department department) {
        // Check if department with same name already exists (for a different ID)
        if (department.getName() != null &&
                departmentRepository.findByName(department.getName()).isPresent() &&
                (department.getId() == null ||
                        !departmentRepository.findByName(department.getName()).get().getId().equals(department.getId()))) {
            throw new RuntimeException("Department with name '" + department.getName() + "' already exists");
        }

        return departmentRepository.save(department);
    }

    /**
     * Deletes a department
     * @param id Department ID
     */
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = getDepartmentById(id);

        // Check if department has employees
        if (!department.getEmployees().isEmpty()) {
            throw new RuntimeException("Cannot delete department with employees. Reassign employees first.");
        }

        departmentRepository.deleteById(id);
    }

    /**
     * Finds departments managed by an employee
     * @param managerId Manager ID
     * @return List of departments managed by the employee
     */
    public List<Department> getDepartmentsByManager(Long managerId) {
        return departmentRepository.findByManagerId(managerId);
    }

    /**
     * Searches for departments by name pattern
     * @param namePattern Pattern to search for
     * @return List of departments matching the pattern
     */
    public List<Department> searchDepartmentsByName(String namePattern) {
        return departmentRepository.findByNameContainingIgnoreCase(namePattern);
    }

    /**
     * Assigns a manager to a department
     * @param departmentId Department ID
     * @param managerId Manager ID
     * @return The updated department
     */
    @Transactional
    public Department assignManager(Long departmentId, Long managerId) {
        Department department = getDepartmentById(departmentId);

        if (managerId == null) {
            department.setManager(null);
        } else {
            Employee manager = employeeRepository.findById(managerId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + managerId));
            department.setManager(manager);
        }

        return departmentRepository.save(department);
    }

    /**
     * Retrieves departments with their employee counts
     * @return Map of department names to employee counts
     */
    public Map<String, Long> getDepartmentEmployeeCounts() {
        List<Object[]> results = departmentRepository.findDepartmentsWithEmployeeCount();

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[1],  // Department name
                        result -> (Long) result[2]     // Employee count
                ));
    }

    /**
     * Retrieves departments that have employees
     * @return List of departments with employees
     */
    public List<Department> getDepartmentsWithEmployees() {
        return departmentRepository.findDepartmentsWithEmployees();
    }

    /**
     * Retrieves departments that have no employees
     * @return List of departments without employees
     */
    public List<Department> getDepartmentsWithoutEmployees() {
        return departmentRepository.findDepartmentsWithoutEmployees();
    }

    /**
     * Counts employees in a department
     * @param departmentId Department ID
     * @return Number of employees in the department
     */
    public long countEmployeesInDepartment(Long departmentId) {
        // Verify department exists
        getDepartmentById(departmentId);

        return departmentRepository.countEmployeesByDepartmentId(departmentId);
    }

    /**
     * Transfers employees from one department to another
     * @param sourceDepartmentId Source department ID
     * @param targetDepartmentId Target department ID
     * @return Number of employees transferred
     */
    @Transactional
    public int transferEmployees(Long sourceDepartmentId, Long targetDepartmentId) {
        Department sourceDepartment = getDepartmentById(sourceDepartmentId);
        Department targetDepartment = getDepartmentById(targetDepartmentId);

        List<Employee> employees = sourceDepartment.getEmployees();
        int count = employees.size();

        for (Employee employee : employees) {
            employee.setDepartment(targetDepartment);
            employeeRepository.save(employee);
        }

        return count;
    }
}
