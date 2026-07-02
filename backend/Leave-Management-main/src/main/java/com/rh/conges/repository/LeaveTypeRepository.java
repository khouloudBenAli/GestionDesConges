package com.rh.conges.repository;

import com.rh.conges.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for LeaveType entity providing CRUD operations and custom queries
 */
@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    /**
     * Finds a leave type by its name
     * @param name The leave type name to search for
     * @return The leave type if found
     */
    Optional<LeaveType> findByName(String name);

    /**
     * Checks if a leave type with the given name exists
     * @param name The leave type name to check
     * @return true if a leave type with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds leave types containing the given name pattern (case-insensitive)
     * @param namePattern The pattern to search for in leave type names
     * @return List of leave types matching the pattern
     */
    List<LeaveType> findByNameContainingIgnoreCase(String namePattern);

    /**
     * Finds all active leave types
     * @return List of active leave types
     */
    List<LeaveType> findByActiveTrue();

    /**
     * Finds all inactive leave types
     * @return List of inactive leave types
     */
    List<LeaveType> findByActiveFalse();

    /**
     * Finds all paid leave types
     * @return List of paid leave types
     */
    List<LeaveType> findByPaidLeaveTrue();

    /**
     * Finds all unpaid leave types
     * @return List of unpaid leave types
     */
    List<LeaveType> findByPaidLeaveFalse();

    /**
     * Finds leave types that require documentation
     * @return List of leave types requiring documentation
     */
    List<LeaveType> findByRequiresDocumentationTrue();

    /**
     * Finds leave types that allow carry over to next year
     * @return List of leave types allowing carry over
     */
    List<LeaveType> findByCanCarryOverTrue();

    /**
     * Finds leave types that allow half-day requests
     * @return List of leave types allowing half-day requests
     */
    List<LeaveType> findByAllowHalfDayTrue();

    /**
     * Finds active paid leave types
     * @return List of active paid leave types
     */
    List<LeaveType> findByActiveTrueAndPaidLeaveTrue();

    /**
     * Finds active unpaid leave types
     * @return List of active unpaid leave types
     */
    List<LeaveType> findByActiveTrueAndPaidLeaveFalse();

    /**
     * Finds leave types with default days allocation greater than or equal to a value
     * @param days The minimum number of days
     * @return List of leave types with default days allocation >= days
     */
    List<LeaveType> findByDefaultDaysPerYearGreaterThanEqual(int days);

    /**
     * Finds leave types with default days allocation less than or equal to a value
     * @param days The maximum number of days
     * @return List of leave types with default days allocation <= days
     */
    List<LeaveType> findByDefaultDaysPerYearLessThanEqual(int days);

    /**
     * Finds leave types ordered by default days allocation (descending)
     * @return List of leave types ordered by default days allocation
     */
    @Query("SELECT lt FROM LeaveType lt ORDER BY lt.defaultDaysPerYear DESC")
    List<LeaveType> findAllOrderByDefaultDaysDesc();

    /**
     * Finds leave types that have been used in leave requests
     * @return List of leave types that have been used
     */
    @Query("SELECT DISTINCT lt FROM LeaveType lt JOIN lt.leaveRequests lr")
    List<LeaveType> findLeaveTypesWithRequests();

    /**
     * Finds leave types that have never been used in leave requests
     * @return List of leave types that have never been used
     */
    @Query("SELECT lt FROM LeaveType lt WHERE lt.leaveRequests IS EMPTY")
    List<LeaveType> findLeaveTypesWithoutRequests();

    /**
     * Counts the number of leave requests for each leave type
     * @return List of leave type IDs and their request counts
     */
    @Query("SELECT lt.id, lt.name, COUNT(lr) FROM LeaveType lt LEFT JOIN lt.leaveRequests lr GROUP BY lt.id, lt.name")
    List<Object[]> countRequestsByLeaveType();

    /**
     * Finds leave types with minimum notice days greater than a value
     * @param days The minimum number of notice days
     * @return List of leave types requiring more notice days
     */
    List<LeaveType> findByMinNoticeDaysGreaterThan(int days);
}
