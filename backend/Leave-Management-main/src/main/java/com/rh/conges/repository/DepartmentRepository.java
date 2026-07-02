package com.rh.conges.repository;

import com.rh.conges.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Department entity providing CRUD operations and custom queries
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Finds a department by its name
     * @param name The department name to search for
     * @return The department if found
     */
    Optional<Department> findByName(String name);

    /**
     * Checks if a department with the given name exists
     * @param name The department name to check
     * @return true if a department with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds departments managed by a specific employee
     * @param managerId The ID of the manager
     * @return List of departments managed by the employee
     */
    List<Department> findByManagerId(Long managerId);

    /**
     * Finds departments containing the given name pattern
     * @param namePattern The pattern to search for in department names
     * @return List of departments matching the pattern
     */
    List<Department> findByNameContainingIgnoreCase(String namePattern);

    /**
     * Finds departments that have at least one employee
     * @return List of departments with employees
     */
    @Query("SELECT DISTINCT d FROM Department d JOIN d.employees e")
    List<Department> findDepartmentsWithEmployees();

    /**
     * Finds departments that have no employees
     * @return List of departments without employees
     */
    @Query("SELECT d FROM Department d WHERE d.employees IS EMPTY")
    List<Department> findDepartmentsWithoutEmployees();

    /**
     * Counts the number of employees in a department
     * @param departmentId The department ID
     * @return The number of employees in the department
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    long countEmployeesByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * Finds departments with employee count
     * @return List of departments with their employee counts
     */
    @Query("SELECT d.id, d.name, COUNT(e) FROM Department d LEFT JOIN d.employees e GROUP BY d.id, d.name")
    List<Object[]> findDepartmentsWithEmployeeCount();

    /**
     * Finds departments with a specific manager and containing the given name pattern
     * @param managerId The ID of the manager
     * @param namePattern The pattern to search for in department names
     * @return List of matching departments
     */
    List<Department> findByManagerIdAndNameContainingIgnoreCase(Long managerId, String namePattern);
}
