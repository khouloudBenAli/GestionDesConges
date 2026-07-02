package com.rh.conges.service;

import com.rh.conges.model.Department;
import com.rh.conges.model.Employee;
import com.rh.conges.repository.DepartmentRepository;
import com.rh.conges.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing employees
 */
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * Retrieves all employees
     * @return List of all employees
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Retrieves all employees with pagination
     * @param pageable Pagination information
     * @return Page of employees
     */
    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    /**
     * Retrieves an employee by ID
     * @param id Employee ID
     * @return The employee if found
     * @throws RuntimeException if employee not found
     */
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));
    }

    /**
     * Finds an employee by email
     * @param email Employee email
     * @return The employee if found
     * @throws RuntimeException if employee not found
     */
    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found with email: " + email));
    }

    /**
     * Checks if an employee exists by email
     * @param email Employee email
     * @return true if employee exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return employeeRepository.existsByEmail(email);
    }

    /**
     * Saves an employee
     * @param employee Employee to save
     * @return The saved employee
     */
    @Transactional
    public Employee saveEmployee(Employee employee) {
        // Check if employee with same email already exists (for a different ID)
        if (employee.getEmail() != null &&
                employeeRepository.findByEmail(employee.getEmail()).isPresent() &&
                (employee.getId() == null ||
                        !employeeRepository.findByEmail(employee.getEmail()).get().getId().equals(employee.getId()))) {
            throw new RuntimeException("Employee with email '" + employee.getEmail() + "' already exists");
        }

        return employeeRepository.save(employee);
    }

    /**
     * Deletes an employee
     * @param id Employee ID
     */
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);

        // Check if employee is a manager of other employees
        if (!employee.getSubordinates().isEmpty()) {
            throw new RuntimeException("Cannot delete employee who is a manager. Reassign subordinates first.");
        }

        // Check if employee is a manager of departments
        if (!departmentRepository.findByManagerId(id).isEmpty()) {
            throw new RuntimeException("Cannot delete employee who is a department manager. Reassign departments first.");
        }

        employeeRepository.deleteById(id);
    }

    /**
     * Retrieves employees by department
     * @param departmentId Department ID
     * @return List of employees in the department
     */
    public List<Employee> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    /**
     * Retrieves employees by department with pagination
     * @param departmentId Department ID
     * @param pageable Pagination information
     * @return Page of employees in the department
     */
    public Page<Employee> getEmployeesByDepartment(Long departmentId, Pageable pageable) {
        return employeeRepository.findByDepartmentId(departmentId, pageable);
    }

    /**
     * Retrieves team members for a manager
     * @param managerId Manager ID
     * @return List of employees managed by the manager
     */
    public List<Employee> getTeamMembers(Long managerId) {
        return employeeRepository.findByManagerId(managerId);
    }

    /**
     * Searches for employees by name
     * @param namePattern Pattern to search for
     * @return List of employees matching the pattern
     */
    public List<Employee> searchEmployeesByName(String namePattern) {
        return employeeRepository.findByNameContaining(namePattern);
    }

    /**
     * Searches for employees by name with pagination
     * @param namePattern Pattern to search for
     * @param pageable Pagination information
     * @return Page of employees matching the pattern
     */
    public Page<Employee> searchEmployeesByName(String namePattern, Pageable pageable) {
        return employeeRepository.findByNameContaining(namePattern, pageable);
    }

    /**
     * Assigns an employee to a department
     * @param employeeId Employee ID
     * @param departmentId Department ID
     * @return The updated employee
     */
    @Transactional
    public Employee assignToDepartment(Long employeeId, Long departmentId) {
        Employee employee = getEmployeeById(employeeId);

        if (departmentId == null) {
            employee.setDepartment(null);
        } else {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found with ID: " + departmentId));
            employee.setDepartment(department);
        }

        return employeeRepository.save(employee);
    }

    /**
     * Assigns a manager to an employee
     * @param employeeId Employee ID
     * @param managerId Manager ID
     * @return The updated employee
     */
    @Transactional
    public Employee assignManager(Long employeeId, Long managerId) {
        Employee employee = getEmployeeById(employeeId);

        if (managerId == null) {
            employee.setManager(null);
        } else {
            // Check for circular management hierarchy
            if (employeeId.equals(managerId)) {
                throw new RuntimeException("An employee cannot be their own manager");
            }

            Employee manager = getEmployeeById(managerId);

            // Check if this would create a circular reference in the management hierarchy
            Employee currentManager = manager.getManager();
            while (currentManager != null) {
                if (currentManager.getId().equals(employeeId)) {
                    throw new RuntimeException("Circular management hierarchy detected");
                }
                currentManager = currentManager.getManager();
            }

            employee.setManager(manager);
        }

        return employeeRepository.save(employee);
    }

    /**
     * Retrieves employees hired after a specific date
     * @param date The hire date to compare against
     * @return List of employees hired after the date
     */
    public List<Employee> getEmployeesHiredAfter(LocalDate date) {
        return employeeRepository.findByHireDateAfter(date);
    }

    /**
     * Retrieves employees hired between two dates
     * @param startDate The start date
     * @param endDate The end date
     * @return List of employees hired between the dates
     */
    public List<Employee> getEmployeesHiredBetween(LocalDate startDate, LocalDate endDate) {
        return employeeRepository.findByHireDateBetween(startDate, endDate);
    }

    /**
     * Retrieves employees by job title
     * @param jobTitle The job title
     * @return List of employees with the job title
     */
    public List<Employee> getEmployeesByJobTitle(String jobTitle) {
        return employeeRepository.findByJobTitle(jobTitle);
    }

    /**
     * Retrieves all managers
     * @return List of employees who are managers
     */
    public List<Employee> getAllManagers() {
        return employeeRepository.findManagers();
    }

    /**
     * Retrieves employees on leave during a specific period
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     * @return List of employees on leave during the period
     */
    public List<Employee> getEmployeesOnLeave(LocalDate startDate, LocalDate endDate) {
        return employeeRepository.findEmployeesOnLeave(startDate, endDate);
    }

    /**
     * Counts employees by job title
     * @return Map of job titles to employee counts
     */
    public Map<String, Long> countEmployeesByJobTitle() {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getJobTitle() != null && !e.getJobTitle().isEmpty())
                .collect(Collectors.groupingBy(Employee::getJobTitle, Collectors.counting()));
    }

    /**
     * Updates an employee's job title
     * @param employeeId Employee ID
     * @param jobTitle New job title
     * @return The updated employee
     */
    @Transactional
    public Employee updateJobTitle(Long employeeId, String jobTitle) {
        Employee employee = getEmployeeById(employeeId);
        employee.setJobTitle(jobTitle);
        return employeeRepository.save(employee);
    }
}
