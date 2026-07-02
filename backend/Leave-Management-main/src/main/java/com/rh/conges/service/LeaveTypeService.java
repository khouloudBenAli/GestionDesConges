package com.rh.conges.service;

import com.rh.conges.model.LeaveType;
import com.rh.conges.repository.LeaveBalanceRepository;
import com.rh.conges.repository.LeaveRequestRepository;
import com.rh.conges.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing leave types
 */
@Service
public class LeaveTypeService {

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    /**
     * Retrieves all leave types
     * @return List of all leave types
     */
    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    /**
     * Retrieves all active leave types
     * @return List of active leave types
     */
    public List<LeaveType> getActiveLeaveTypes() {
        return leaveTypeRepository.findByActiveTrue();
    }

    /**
     * Retrieves a leave type by ID
     * @param id Leave type ID
     * @return The leave type if found
     * @throws RuntimeException if leave type not found
     */
    public LeaveType getLeaveTypeById(Long id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave type not found with ID: " + id));
    }

    /**
     * Retrieves a leave type by name
     * @param name Leave type name
     * @return The leave type if found
     * @throws RuntimeException if leave type not found
     */
    public LeaveType getLeaveTypeByName(String name) {
        return leaveTypeRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Leave type not found with name: " + name));
    }

    /**
     * Checks if a leave type exists by name
     * @param name Leave type name
     * @return true if leave type exists, false otherwise
     */
    public boolean existsByName(String name) {
        return leaveTypeRepository.existsByName(name);
    }

    /**
     * Saves a leave type
     * @param leaveType Leave type to save
     * @return The saved leave type
     */
    @Transactional
    public LeaveType saveLeaveType(LeaveType leaveType) {
        // Check if leave type with same name already exists (for a different ID)
        if (leaveType.getName() != null &&
                leaveTypeRepository.findByName(leaveType.getName()).isPresent() &&
                (leaveType.getId() == null ||
                        !leaveTypeRepository.findByName(leaveType.getName()).get().getId().equals(leaveType.getId()))) {
            throw new RuntimeException("Leave type with name '" + leaveType.getName() + "' already exists");
        }

        // Validate fields
        if (leaveType.getDefaultDaysPerYear() == null) {
            throw new RuntimeException("Default days per year is required");
        }

        if (leaveType.getDefaultDaysPerYear() < 0) {
            throw new RuntimeException("Default days per year cannot be negative");
        }

        if (leaveType.getCanCarryOver() && leaveType.getMaxCarryOverDays() == null) {
            throw new RuntimeException("Maximum carry over days is required when carry over is enabled");
        }

        if (leaveType.getMaxCarryOverDays() != null && leaveType.getMaxCarryOverDays() < 0) {
            throw new RuntimeException("Maximum carry over days cannot be negative");
        }

        if (leaveType.getMinNoticeDays() != null && leaveType.getMinNoticeDays() < 0) {
            throw new RuntimeException("Minimum notice days cannot be negative");
        }

        if (leaveType.getMaxConsecutiveDays() != null && leaveType.getMaxConsecutiveDays() < 0) {
            throw new RuntimeException("Maximum consecutive days cannot be negative");
        }

        // Set default values if not provided
        if (leaveType.getPaidLeave() == null) {
            leaveType.setPaidLeave(true);
        }

        if (leaveType.getActive() == null) {
            leaveType.setActive(true);
        }

        if (leaveType.getRequiresDocumentation() == null) {
            leaveType.setRequiresDocumentation(false);
        }

        if (leaveType.getCanCarryOver() == null) {
            leaveType.setCanCarryOver(false);
        }

        if (leaveType.getAllowHalfDay() == null) {
            leaveType.setAllowHalfDay(true);
        }

        return leaveTypeRepository.save(leaveType);
    }

    /**
     * Deletes a leave type
     * @param id Leave type ID
     */
    @Transactional
    public void deleteLeaveType(Long id) {
        LeaveType leaveType = getLeaveTypeById(id);

        // Check if leave type is used in leave requests
        if (!leaveType.getLeaveRequests().isEmpty()) {
            throw new RuntimeException("Cannot delete leave type that is used in leave requests");
        }

        // Check if leave type is used in leave balances
        if (!leaveType.getLeaveBalances().isEmpty()) {
            throw new RuntimeException("Cannot delete leave type that is used in leave balances");
        }

        leaveTypeRepository.deleteById(id);
    }

    /**
     * Activates or deactivates a leave type
     * @param id Leave type ID
     * @param active Active status
     * @return The updated leave type
     */
    @Transactional
    public LeaveType setActiveStatus(Long id, boolean active) {
        LeaveType leaveType = getLeaveTypeById(id);
        leaveType.setActive(active);
        return leaveTypeRepository.save(leaveType);
    }

    /**
     * Searches for leave types by name
     * @param namePattern Pattern to search for
     * @return List of leave types matching the pattern
     */
    public List<LeaveType> searchLeaveTypesByName(String namePattern) {
        return leaveTypeRepository.findByNameContainingIgnoreCase(namePattern);
    }

    /**
     * Retrieves all paid leave types
     * @return List of paid leave types
     */
    public List<LeaveType> getPaidLeaveTypes() {
        return leaveTypeRepository.findByPaidLeaveTrue();
    }

    /**
     * Retrieves all unpaid leave types
     * @return List of unpaid leave types
     */
    public List<LeaveType> getUnpaidLeaveTypes() {
        return leaveTypeRepository.findByPaidLeaveFalse();
    }

    /**
     * Retrieves leave types that require documentation
     * @return List of leave types requiring documentation
     */
    public List<LeaveType> getLeaveTypesRequiringDocumentation() {
        return leaveTypeRepository.findByRequiresDocumentationTrue();
    }

    /**
     * Retrieves leave types that allow carry over
     * @return List of leave types allowing carry over
     */
    public List<LeaveType> getLeaveTypesAllowingCarryOver() {
        return leaveTypeRepository.findByCanCarryOverTrue();
    }

    /**
     * Retrieves leave types that allow half-day requests
     * @return List of leave types allowing half-day requests
     */
    public List<LeaveType> getLeaveTypesAllowingHalfDay() {
        return leaveTypeRepository.findByAllowHalfDayTrue();
    }

    /**
     * Retrieves leave types with default days allocation greater than or equal to a value
     * @param days The minimum number of days
     * @return List of leave types with default days allocation >= days
     */
    public List<LeaveType> getLeaveTypesWithMinimumDays(int days) {
        return leaveTypeRepository.findByDefaultDaysPerYearGreaterThanEqual(days);
    }

    /**
     * Retrieves leave types ordered by default days allocation (descending)
     * @return List of leave types ordered by default days allocation
     */
    public List<LeaveType> getLeaveTypesOrderedByDays() {
        return leaveTypeRepository.findAllOrderByDefaultDaysDesc();
    }

    /**
     * Retrieves leave types that have been used in leave requests
     * @return List of leave types that have been used
     */
    public List<LeaveType> getLeaveTypesWithRequests() {
        return leaveTypeRepository.findLeaveTypesWithRequests();
    }

    /**
     * Retrieves leave types that have never been used in leave requests
     * @return List of leave types that have never been used
     */
    public List<LeaveType> getLeaveTypesWithoutRequests() {
        return leaveTypeRepository.findLeaveTypesWithoutRequests();
    }

    /**
     * Counts the number of leave requests for each leave type
     * @return Map of leave type names to request counts
     */
    public Map<String, Long> countRequestsByLeaveType() {
        List<Object[]> results = leaveTypeRepository.countRequestsByLeaveType();

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[1],  // Leave type name
                        result -> (Long) result[2]     // Request count
                ));
    }

    /**
     * Updates the default days allocation for a leave type
     * @param id Leave type ID
     * @param defaultDays New default days allocation
     * @return The updated leave type
     */
    @Transactional
    public LeaveType updateDefaultDays(Long id, int defaultDays) {
        if (defaultDays < 0) {
            throw new RuntimeException("Default days cannot be negative");
        }

        LeaveType leaveType = getLeaveTypeById(id);
        leaveType.setDefaultDaysPerYear(defaultDays);
        return leaveTypeRepository.save(leaveType);
    }

    /**
     * Updates the carry over settings for a leave type
     * @param id Leave type ID
     * @param canCarryOver Whether carry over is allowed
     * @param maxCarryOverDays Maximum days that can be carried over
     * @return The updated leave type
     */
    @Transactional
    public LeaveType updateCarryOverSettings(Long id, boolean canCarryOver, Integer maxCarryOverDays) {
        if (canCarryOver && maxCarryOverDays == null) {
            throw new RuntimeException("Maximum carry over days is required when carry over is enabled");
        }

        if (maxCarryOverDays != null && maxCarryOverDays < 0) {
            throw new RuntimeException("Maximum carry over days cannot be negative");
        }

        LeaveType leaveType = getLeaveTypeById(id);
        leaveType.setCanCarryOver(canCarryOver);
        leaveType.setMaxCarryOverDays(maxCarryOverDays);
        return leaveTypeRepository.save(leaveType);
    }

    /**
     * Updates the documentation requirement for a leave type
     * @param id Leave type ID
     * @param requiresDocumentation Whether documentation is required
     * @return The updated leave type
     */
    @Transactional
    public LeaveType updateDocumentationRequirement(Long id, boolean requiresDocumentation) {
        LeaveType leaveType = getLeaveTypeById(id);
        leaveType.setRequiresDocumentation(requiresDocumentation);
        return leaveTypeRepository.save(leaveType);
    }

    /**
     * Updates the half-day allowance for a leave type
     * @param id Leave type ID
     * @param allowHalfDay Whether half-day requests are allowed
     * @return The updated leave type
     */
    @Transactional
    public LeaveType updateHalfDayAllowance(Long id, boolean allowHalfDay) {
        LeaveType leaveType = getLeaveTypeById(id);
        leaveType.setAllowHalfDay(allowHalfDay);
        return leaveTypeRepository.save(leaveType);
    }
}
