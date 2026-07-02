package com.rh.conges.repository;

import com.rh.conges.model.LeaveRequest;
import com.rh.conges.model.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for LeaveRequest entity providing CRUD operations and custom queries
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    /**
     * Finds leave requests for a specific employee
     * @param employeeId The employee ID
     * @return List of leave requests for the employee
     */
    List<LeaveRequest> findByEmployeeId(Long employeeId);

    /**
     * Finds leave requests for a specific employee with pagination
     * @param employeeId The employee ID
     * @param pageable Pagination information
     * @return Page of leave requests for the employee
     */
    Page<LeaveRequest> findByEmployeeId(Long employeeId, Pageable pageable);

    /**
     * Finds leave requests for employees managed by a specific manager
     * @param managerId The manager ID
     * @return List of leave requests for employees under the manager
     */
    List<LeaveRequest> findByEmployeeManagerId(Long managerId);

    /**
     * Finds leave requests for employees managed by a specific manager with pagination
     * @param managerId The manager ID
     * @param pageable Pagination information
     * @return Page of leave requests for employees under the manager
     */
    Page<LeaveRequest> findByEmployeeManagerId(Long managerId, Pageable pageable);

    /**
     * Finds leave requests with a specific status
     * @param status The leave request status
     * @return List of leave requests with the status
     */
    List<LeaveRequest> findByStatus(LeaveStatus status);

    /**
     * Finds leave requests with a specific status with pagination
     * @param status The leave request status
     * @param pageable Pagination information
     * @return Page of leave requests with the status
     */
    Page<LeaveRequest> findByStatus(LeaveStatus status, Pageable pageable);

    /**
     * Finds leave requests for a specific employee with a specific status
     * @param employeeId The employee ID
     * @param status The leave request status
     * @return List of leave requests for the employee with the status
     */
    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    /**
     * Finds leave requests for a specific leave type
     * @param leaveTypeId The leave type ID
     * @return List of leave requests for the leave type
     */
    List<LeaveRequest> findByLeaveTypeId(Long leaveTypeId);

    /**
     * Finds leave requests submitted after a specific date
     * @param date The date to compare against
     * @return List of leave requests submitted after the date
     */
    List<LeaveRequest> findByRequestDateAfter(LocalDateTime date);

    /**
     * Finds leave requests with start date after a specific date
     * @param date The date to compare against
     * @return List of leave requests starting after the date
     */
    List<LeaveRequest> findByStartDateAfter(LocalDate date);

    /**
     * Finds leave requests with start date before a specific date
     * @param date The date to compare against
     * @return List of leave requests starting before the date
     */
    List<LeaveRequest> findByStartDateBefore(LocalDate date);

    /**
     * Finds leave requests for a specific period
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     * @return List of leave requests overlapping with the period
     */
    @Query("SELECT lr FROM LeaveRequest lr WHERE " +
            "((lr.startDate BETWEEN :startDate AND :endDate) OR " +
            "(lr.endDate BETWEEN :startDate AND :endDate) OR " +
            "(lr.startDate <= :startDate AND lr.endDate >= :endDate))")
    List<LeaveRequest> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Finds approved leave requests for a specific period
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     * @return List of approved leave requests overlapping with the period
     */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = 'APPROVED' AND " +
            "((lr.startDate BETWEEN :startDate AND :endDate) OR " +
            "(lr.endDate BETWEEN :startDate AND :endDate) OR " +
            "(lr.startDate <= :startDate AND lr.endDate >= :endDate))")
    List<LeaveRequest> findApprovedByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Finds leave requests for employees in a specific department
     * @param departmentId The department ID
     * @return List of leave requests for employees in the department
     */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.department.id = :departmentId")
    List<LeaveRequest> findByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * Finds leave requests for employees in a specific department with a specific status
     * @param departmentId The department ID
     * @param status The leave request status
     * @return List of leave requests for employees in the department with the status
     */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.department.id = :departmentId AND lr.status = :status")
    List<LeaveRequest> findByDepartmentIdAndStatus(
            @Param("departmentId") Long departmentId,
            @Param("status") LeaveStatus status);

    /**
     * Finds pending leave requests for a specific manager
     * @param managerId The manager ID
     * @return List of pending leave requests for employees under the manager
     */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.manager.id = :managerId AND lr.status = 'PENDING'")
    List<LeaveRequest> findPendingByManagerId(@Param("managerId") Long managerId);

    /**
     * Counts leave requests by status
     * @param status The leave request status
     * @return Number of leave requests with the status
     */
    long countByStatus(LeaveStatus status);

    /**
     * Counts leave requests by employee and status
     * @param employeeId The employee ID
     * @param status The leave request status
     * @return Number of leave requests for the employee with the status
     */
    long countByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    /**
     * Finds emergency leave requests
     * @return List of emergency leave requests
     */
    List<LeaveRequest> findByIsEmergencyTrue();

    /**
     * Finds leave requests requiring documentation
     * @return List of leave requests requiring documentation
     */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.leaveType.requiresDocumentation = true AND lr.attachmentPath IS NULL")
    List<LeaveRequest> findRequestsRequiringDocumentation();

    /**
     * Finds leave requests submitted in the last N days
     * @param date Number of days to look back
     * @return List of leave requests submitted in the last N days
     */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.requestDate >= :date")
    List<LeaveRequest> findRequestsSubmittedInLastNDays(@Param("date") LocalDateTime date);

    /**
     * Finds leave requests with long duration (more than specified days)
     * @param days Minimum duration in days
     * @return List of leave requests with long duration
     */
    @Query("SELECT lr FROM LeaveRequest lr WHERE DATEDIFF(lr.endDate, lr.startDate) + 1 > :days")
    List<LeaveRequest> findLongDurationRequests(@Param("days") int days);
}
