package com.rh.conges.repository;

import com.rh.conges.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Employee entity providing CRUD operations and custom queries
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Finds an employee by email address
     * @param email The email address to search for
     * @return The employee if found
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Checks if an employee with the given email exists
     * @param email The email to check
     * @return true if an employee with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds employees by department ID
     * @param departmentId The department ID
     * @return List of employees in the department
     */
    List<Employee> findByDepartmentId(Long departmentId);

    /**
     * Finds employees by department ID with pagination
     * @param departmentId The department ID
     * @param pageable Pagination information
     * @return Page of employees in the department
     */
    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    /**
     * Finds employees by manager ID
     * @param managerId The manager ID
     * @return List of employees managed by the manager
     */
    List<Employee> findByManagerId(Long managerId);

    /**
     * Finds employees by first name and last name
     * @param firstName The first name to search for
     * @param lastName The last name to search for
     * @return List of matching employees
     */
    List<Employee> findByFirstNameAndLastName(String firstName, String lastName);

    /**
     * Finds employees by first name or last name containing the given pattern (case-insensitive)
     * @param pattern The pattern to search for
     * @return List of matching employees
     */
    @Query("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :pattern, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :pattern, '%'))")
    List<Employee> findByNameContaining(@Param("pattern") String pattern);

    /**
     * Finds employees by first name or last name containing the given pattern (case-insensitive) with pagination
     * @param pattern The pattern to search for
     * @param pageable Pagination information
     * @return Page of matching employees
     */
    @Query("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :pattern, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :pattern, '%'))")
    Page<Employee> findByNameContaining(@Param("pattern") String pattern, Pageable pageable);

    /**
     * Finds employees hired after a specific date
     * @param date The hire date to compare against
     * @return List of employees hired after the date
     */
    List<Employee> findByHireDateAfter(LocalDate date);

    /**
     * Finds employees hired between two dates
     * @param startDate The start date
     * @param endDate The end date
     * @return List of employees hired between the dates
     */
    List<Employee> findByHireDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Finds employees by job title
     * @param jobTitle The job title to search for
     * @return List of employees with the job title
     */
    List<Employee> findByJobTitle(String jobTitle);

    /**
     * Finds employees by job title containing the given pattern (case-insensitive)
     * @param pattern The pattern to search for
     * @return List of employees with matching job titles
     */
    List<Employee> findByJobTitleContainingIgnoreCase(String pattern);

    /**
     * Finds employees without a manager
     * @return List of employees without a manager
     */
    List<Employee> findByManagerIsNull();

    /**
     * Finds employees without a department
     * @return List of employees without a department
     */
    List<Employee> findByDepartmentIsNull();

    /**
     * Finds employees by department ID and job title
     * @param departmentId The department ID
     * @param jobTitle The job title
     * @return List of matching employees
     */
    List<Employee> findByDepartmentIdAndJobTitle(Long departmentId, String jobTitle);

    /**
     * Counts employees by department ID
     * @param departmentId The department ID
     * @return Number of employees in the department
     */
    long countByDepartmentId(Long departmentId);

    /**
     * Finds employees who are managers
     * @return List of employees who are managers
     */
    @Query("SELECT DISTINCT e FROM Employee e WHERE EXISTS (SELECT 1 FROM Employee s WHERE s.manager = e)")
    List<Employee> findManagers();

    /**
     * Finds employees who are on leave during a specific period
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     * @return List of employees on leave during the period
     */
    @Query("SELECT DISTINCT e FROM Employee e JOIN e.leaveRequests lr WHERE lr.status = 'APPROVED' AND " +
            "((lr.startDate BETWEEN :startDate AND :endDate) OR " +
            "(lr.endDate BETWEEN :startDate AND :endDate) OR " +
            "(lr.startDate <= :startDate AND lr.endDate >= :endDate))")
    List<Employee> findEmployeesOnLeave(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
