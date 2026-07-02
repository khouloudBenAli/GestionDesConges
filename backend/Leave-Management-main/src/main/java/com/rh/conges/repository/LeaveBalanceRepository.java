package com.rh.conges.repository;

import com.rh.conges.model.LeaveBalance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for LeaveBalance entity providing CRUD operations and custom queries
 */
@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    /**
     * Finds leave balances for a specific employee
     * @param employeeId The employee ID
     * @return List of leave balances for the employee
     */
    List<LeaveBalance> findByEmployeeId(Long employeeId);

    /**
     * Finds leave balances for a specific employee in a specific year
     * @param employeeId The employee ID
     * @param year The year
     * @return List of leave balances for the employee in the year
     */
    List<LeaveBalance> findByEmployeeIdAndYear(Long employeeId, int year);

    /**
     * Finds leave balances for a specific leave type
     * @param leaveTypeId The leave type ID
     * @return List of leave balances for the leave type
     */
    List<LeaveBalance> findByLeaveTypeId(Long leaveTypeId);

    /**
     * Finds leave balances for a specific leave type and year
     * @param leaveTypeId The leave type ID
     * @param year The year
     * @return List of leave balances for the leave type in the year
     */
    List<LeaveBalance> findByLeaveTypeIdAndYear(Long leaveTypeId, int year);

    /**
     * Finds leave balances for a specific year
     * @param year The year
     * @return List of leave balances for the year
     */
    List<LeaveBalance> findByYear(int year);

    /**
     * Finds a specific leave balance by employee, leave type, and year
     * @param employeeId The employee ID
     * @param leaveTypeId The leave type ID
     * @param year The year
     * @return The leave balance if found
     */
    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(Long employeeId, Long leaveTypeId, int year);

    /**
     * Finds leave balances for employees in a specific department
     * @param departmentId The department ID
     * @param year The year
     * @return List of leave balances for employees in the department for the year
     */
    @Query("SELECT lb FROM LeaveBalance lb WHERE lb.employee.department.id = :departmentId AND lb.year = :year")
    List<LeaveBalance> findByDepartmentIdAndYear(@Param("departmentId") Long departmentId, @Param("year") int year);

    /**
     * Finds leave balances for employees under a specific manager
     * @param managerId The manager ID
     * @param year The year
     * @return List of leave balances for employees under the manager for the year
     */
    @Query("SELECT lb FROM LeaveBalance lb WHERE lb.employee.manager.id = :managerId AND lb.year = :year")
    List<LeaveBalance> findByManagerIdAndYear(@Param("managerId") Long managerId, @Param("year") int year);

    /**
     * Finds leave balances with remaining days greater than zero
     * @param year The year
     * @return List of leave balances with remaining days
     */
    @Query("SELECT lb FROM LeaveBalance lb WHERE lb.year = :year AND (lb.totalDays + lb.carriedOverDays + lb.additionalDays - lb.usedDays) > 0")
    List<LeaveBalance> findWithRemainingDays(@Param("year") int year);

    /**
     * Finds leave balances with zero remaining days
     * @param year The year
     * @return List of leave balances with no remaining days
     */
    @Query("SELECT lb FROM LeaveBalance lb WHERE lb.year = :year AND (lb.totalDays + lb.carriedOverDays + lb.additionalDays - lb.usedDays) <= 0")
    List<LeaveBalance> findWithNoRemainingDays(@Param("year") int year);

    /**
     * Calculates the total used days for a specific leave type across all employees
     * @param leaveTypeId The leave type ID
     * @param year The year
     * @return The total used days
     */
    @Query("SELECT SUM(lb.usedDays) FROM LeaveBalance lb WHERE lb.leaveType.id = :leaveTypeId AND lb.year = :year")
    Integer sumUsedDaysByLeaveTypeAndYear(@Param("leaveTypeId") Long leaveTypeId, @Param("year") int year);

    /**
     * Calculates the average used days per employee for a specific leave type
     * @param leaveTypeId The leave type ID
     * @param year The year
     * @return The average used days
     */
    @Query("SELECT AVG(lb.usedDays) FROM LeaveBalance lb WHERE lb.leaveType.id = :leaveTypeId AND lb.year = :year")
    Double avgUsedDaysByLeaveTypeAndYear(@Param("leaveTypeId") Long leaveTypeId, @Param("year") int year);

    /**
     * Finds employees with the highest leave usage for a specific leave type
     * @param leaveTypeId The leave type ID
     * @param year The year
     * @param pageable Pagination information (page number and size)
     * @return List of leave balances ordered by used days (descending)
     */
    @Query("SELECT lb FROM LeaveBalance lb WHERE lb.leaveType.id = :leaveTypeId AND lb.year = :year ORDER BY lb.usedDays DESC")
    List<LeaveBalance> findTopLeaveUsers(@Param("leaveTypeId") Long leaveTypeId, @Param("year") int year, Pageable pageable);

    /**
     * Checks if a leave balance exists for a specific employee, leave type, and year
     * @param employeeId The employee ID
     * @param leaveTypeId The leave type ID
     * @param year The year
     * @return true if the leave balance exists, false otherwise
     */
    boolean existsByEmployeeIdAndLeaveTypeIdAndYear(Long employeeId, Long leaveTypeId, int year);

    /**
     * Deletes leave balances for a specific year
     * @param year The year
     * @return The number of records deleted
     */
    long deleteByYear(int year);

    /**
     * Counts leave balances by leave type and year
     * @param leaveTypeId The leave type ID
     * @param year The year
     * @return The count of leave balances
     */
    long countByLeaveTypeIdAndYear(Long leaveTypeId, int year);
}
