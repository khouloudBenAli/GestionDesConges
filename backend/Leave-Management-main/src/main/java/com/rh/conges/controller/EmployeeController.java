package com.rh.conges.controller;

import com.rh.conges.model.Employee;
import com.rh.conges.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * Retrieves all employees
     * @return List of employees
     */
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    /**
     * Retrieves an employee by ID
     * @param id Employee ID
     * @return The found employee
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Creates a new employee
     * @param employee Employee data to create
     * @return The created employee
     */
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeService.saveEmployee(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    /**
     * Updates an existing employee
     * @param id ID of the employee to update
     * @param employee New employee data
     * @return The updated employee
     */
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        try {
            // Check if employee exists
            employeeService.getEmployeeById(id);

            // Update the employee
            employee.setId(id);
            Employee updatedEmployee = employeeService.saveEmployee(employee);

            return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes an employee
     * @param id ID of the employee to delete
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        try {
            // Check if employee exists
            employeeService.getEmployeeById(id);

            // Delete the employee
            employeeService.deleteEmployee(id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves employees by department
     * @param departmentId Department ID
     * @return List of employees in the department
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        List<Employee> employees = employeeService.getEmployeesByDepartment(departmentId);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    /**
     * Retrieves team members for a manager
     * @param managerId Manager ID
     * @return List of employees managed by the manager
     */
    @GetMapping("/team/{managerId}")
    public ResponseEntity<List<Employee>> getTeamMembers(@PathVariable Long managerId) {
        List<Employee> teamMembers = employeeService.getTeamMembers(managerId);
        return new ResponseEntity<>(teamMembers, HttpStatus.OK);
    }

    /**
     * Finds an employee by email
     * @param email Employee email
     * @return The found employee
     */
    @GetMapping("/email")
    public ResponseEntity<Employee> findByEmail(@RequestParam String email) {
        Employee employee = employeeService.findByEmail(email);
        if (employee != null) {
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
