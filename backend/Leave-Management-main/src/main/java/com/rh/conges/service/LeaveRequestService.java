package com.rh.conges.service;

import com.rh.conges.model.Employee;
import com.rh.conges.model.LeaveRequest;
import com.rh.conges.model.LeaveStatus;
import com.rh.conges.model.LeaveType;
import com.rh.conges.repository.EmployeeRepository;
import com.rh.conges.repository.LeaveRequestRepository;
import com.rh.conges.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing leave requests
 */
@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    /**
     * Retrieves all leave requests
     * @return List of all leave requests
     */
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    /**
     * Retrieves all leave requests with pagination
     * @param pageable Pagination information
     * @return Page of leave requests
     */
    public Page<LeaveRequest> getAllLeaveRequests(Pageable pageable) {
        return leaveRequestRepository.findAll(pageable);
    }

    /**
     * Retrieves a leave request by ID
     * @param id Leave request ID
     * @return The leave request if found
     * @throws RuntimeException if leave request not found
     */
    public LeaveRequest getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found with ID: " + id));
    }

    /**
     * Creates a new leave request
     * @param leaveRequest Leave request to create
     * @return The created leave request
     */
    @Transactional
    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest) {
        // Validate employee
        Employee employee = employeeRepository.findById(leaveRequest.getEmployee().getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Validate leave type
        LeaveType leaveType = leaveTypeRepository.findById(leaveRequest.getLeaveType().getId())
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        // Check if leave type is active
        if (!leaveType.isAvailable()) {
            throw new RuntimeException("Leave type is not available for requests");
        }

        // Validate dates
        if (leaveRequest.getStartDate() == null || leaveRequest.getEndDate() == null) {
            throw new RuntimeException("Start date and end date are required");
        }

        if (leaveRequest.getStartDate().isAfter(leaveRequest.getEndDate())) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        // Check if start date is in the past
        if (leaveRequest.getStartDate().isBefore(LocalDate.now()) && !leaveRequest.getIsEmergency()) {
            throw new RuntimeException("Start date cannot be in the past for non-emergency leave");
        }

        // Check minimum notice days
        if (leaveType.getMinNoticeDays() != null && leaveType.getMinNoticeDays() > 0 && !leaveRequest.getIsEmergency()) {
            long daysNotice = ChronoUnit.DAYS.between(LocalDate.now(), leaveRequest.getStartDate());
            if (daysNotice < leaveType.getMinNoticeDays()) {
                throw new RuntimeException("Minimum notice of " + leaveType.getMinNoticeDays() +
                        " days required for this leave type");
            }
        }

        // Check maximum consecutive days
        if (leaveType.getMaxConsecutiveDays() != null && leaveType.getMaxConsecutiveDays() > 0) {
            long requestedDays = ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;
            if (requestedDays > leaveType.getMaxConsecutiveDays()) {
                throw new RuntimeException("Maximum consecutive days allowed is " +
                        leaveType.getMaxConsecutiveDays());
            }
        }

        // Check half day settings
        if ((leaveRequest.getHalfDayStart() || leaveRequest.getHalfDayEnd()) &&
                (leaveType.getAllowHalfDay() == null || !leaveType.getAllowHalfDay())) {
            throw new RuntimeException("Half-day leave is not allowed for this leave type");
        }

        // Calculate duration
        int durationInDays = leaveRequest.getDurationInDays();

        // Check for overlapping approved leave
        List<LeaveRequest> overlappingRequests = leaveRequestRepository.findApprovedByDateRange(
                leaveRequest.getStartDate(), leaveRequest.getEndDate());

        overlappingRequests = overlappingRequests.stream()
                .filter(request -> request.getEmployee().getId().equals(employee.getId()))
                .toList();

        if (!overlappingRequests.isEmpty()) {
            throw new RuntimeException("Employee already has approved leave during this period");
        }

        // Check leave balance if it's a paid leave type
        if (leaveType.getPaidLeave()) {
            int year = leaveRequest.getStartDate().getYear();

            // If leave spans multiple years, check balance for each year
            if (leaveRequest.getEndDate().getYear() > year) {
                throw new RuntimeException("Leave requests spanning multiple years are not supported");
            }

            if (!leaveBalanceService.hasSufficientBalance(
                    employee.getId(), leaveType.getId(), year, durationInDays)) {
                throw new RuntimeException("Insufficient leave balance");
            }
        }

        // Set default values
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest.setRequestDate(LocalDateTime.now());

        // Save the request
        return leaveRequestRepository.save(leaveRequest);
    }

    /**
     * Updates a leave request
     * @param id Leave request ID
     * @param leaveRequest Updated leave request data
     * @return The updated leave request
     */
    @Transactional
    public LeaveRequest updateLeaveRequest(Long id, LeaveRequest leaveRequest) {
        LeaveRequest existingRequest = getLeaveRequestById(id);

        // Only allow updates to PENDING requests
        if (existingRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Cannot update a leave request that is not in PENDING status");
        }

        // Update fields
        existingRequest.setStartDate(leaveRequest.getStartDate());
        existingRequest.setEndDate(leaveRequest.getEndDate());
        existingRequest.setHalfDayStart(leaveRequest.getHalfDayStart());
        existingRequest.setHalfDayEnd(leaveRequest.getHalfDayEnd());
        existingRequest.setReason(leaveRequest.getReason());
        existingRequest.setIsEmergency(leaveRequest.getIsEmergency());
        existingRequest.setContactInfo(leaveRequest.getContactInfo());

        // If leave type is changed, validate it
        if (!existingRequest.getLeaveType().getId().equals(leaveRequest.getLeaveType().getId())) {
            LeaveType leaveType = leaveTypeRepository.findById(leaveRequest.getLeaveType().getId())
                    .orElseThrow(() -> new RuntimeException("Leave type not found"));

            if (!leaveType.isAvailable()) {
                throw new RuntimeException("Leave type is not available for requests");
            }

            existingRequest.setLeaveType(leaveType);
        }

        // Validate dates
        if (existingRequest.getStartDate().isAfter(existingRequest.getEndDate())) {
            throw new RuntimeException("Start date cannot be after end date");
        }

        // Re-check leave balance if it's a paid leave type
        if (existingRequest.getLeaveType().getPaidLeave()) {
            int year = existingRequest.getStartDate().getYear();
            int durationInDays = existingRequest.getDurationInDays();

            if (!leaveBalanceService.hasSufficientBalance(
                    existingRequest.getEmployee().getId(),
                    existingRequest.getLeaveType().getId(),
                    year,
                    durationInDays)) {
                throw new RuntimeException("Insufficient leave balance");
            }
        }

        return leaveRequestRepository.save(existingRequest);
    }

    /**
     * Approves a leave request
     * @param id Leave request ID
     * @param managerComment Comment from the manager
     * @return The approved leave request
     */
    @Transactional
    public LeaveRequest approveLeaveRequest(Long id, String managerComment) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);

        // Only allow approval of PENDING requests
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Cannot approve a leave request that is not in PENDING status");
        }

        // Update status and manager comment
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setManagerComment(managerComment);
        leaveRequest.setResponseDate(LocalDateTime.now());

        // Update leave balance if it's a paid leave type
        if (leaveRequest.getLeaveType().getPaidLeave()) {
            int year = leaveRequest.getStartDate().getYear();
            int durationInDays = leaveRequest.getDurationInDays();

            leaveBalanceService.updateUsedDays(
                    leaveRequest.getEmployee().getId(),
                    leaveRequest.getLeaveType().getId(),
                    year,
                    durationInDays);
        }

        return leaveRequestRepository.save(leaveRequest);
    }

    /**
     * Rejects a leave request
     * @param id Leave request ID
     * @param managerComment Comment from the manager
     * @return The rejected leave request
     */
    @Transactional
    public LeaveRequest rejectLeaveRequest(Long id, String managerComment) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);

        // Only allow rejection of PENDING requests
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Cannot reject a leave request that is not in PENDING status");
        }

        // Update status and manager comment
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setManagerComment(managerComment);
        leaveRequest.setResponseDate(LocalDateTime.now());

        return leaveRequestRepository.save(leaveRequest);
    }

    /**
     * Cancels a leave request
     * @param id Leave request ID
     * @return The cancelled leave request
     */
    @Transactional
    public LeaveRequest cancelLeaveRequest(Long id) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);

        // Only allow cancellation of PENDING or APPROVED requests
        if (leaveRequest.getStatus() != LeaveStatus.PENDING && leaveRequest.getStatus() != LeaveStatus.APPROVED) {
            throw new RuntimeException("Cannot cancel a leave request that is not in PENDING or APPROVED status");
        }

        // If the request was approved and it's a paid leave type, restore the leave balance
        if (leaveRequest.getStatus() == LeaveStatus.APPROVED && leaveRequest.getLeaveType().getPaidLeave()) {
            int year = leaveRequest.getStartDate().getYear();
            int durationInDays = leaveRequest.getDurationInDays();

            leaveBalanceService.updateUsedDays(
                    leaveRequest.getEmployee().getId(),
                    leaveRequest.getLeaveType().getId(),
                    year,
                    -durationInDays);  // Negative value to restore balance
        }

        // Update status
        leaveRequest.setStatus(LeaveStatus.CANCELLED);
        leaveRequest.setResponseDate(LocalDateTime.now());

        return leaveRequestRepository.save(leaveRequest);
    }

    /**
     * Retrieves leave requests for a specific employee
     * @param employeeId Employee ID
     * @return List of leave requests for the employee
     */
    public List<LeaveRequest> getEmployeeLeaveRequests(Long employeeId) {
        // Verify employee exists
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    /**
     * Retrieves leave requests for a specific employee with pagination
     * @param employeeId Employee ID
     * @param pageable Pagination information
     * @return Page of leave requests for the employee
     */
    public Page<LeaveRequest> getEmployeeLeaveRequests(Long employeeId, Pageable pageable) {
        // Verify employee exists
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        return leaveRequestRepository.findByEmployeeId(employeeId, pageable);
    }

    /**
     * Retrieves leave requests for employees managed by a specific manager
     * @param managerId Manager ID
     * @return List of leave requests for employees under the manager
     */
    public List<LeaveRequest> getTeamLeaveRequests(Long managerId) {
        // Verify manager exists
        employeeRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + managerId));

        return leaveRequestRepository.findByEmployeeManagerId(managerId);
    }

    /**
     * Retrieves pending leave requests for employees managed by a specific manager
     * @param managerId Manager ID
     * @return List of pending leave requests for employees under the manager
     */
    public List<LeaveRequest> getPendingTeamLeaveRequests(Long managerId) {
        // Verify manager exists
        employeeRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + managerId));

        return leaveRequestRepository.findPendingByManagerId(managerId);
    }

    /**
     * Retrieves leave requests for a specific period
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @return List of leave requests for the period
     */
    public List<LeaveRequest> getLeaveRequestsByDateRange(LocalDate startDate, LocalDate endDate) {
        return leaveRequestRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Retrieves approved leave requests for a specific period
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @return List of approved leave requests for the period
     */
    public List<LeaveRequest> getApprovedLeaveRequestsByDateRange(LocalDate startDate, LocalDate endDate) {
        return leaveRequestRepository.findApprovedByDateRange(startDate, endDate);
    }

    /**
     * Retrieves leave requests by status
     * @param status Leave request status
     * @return List of leave requests with the status
     */
    public List<LeaveRequest> getLeaveRequestsByStatus(LeaveStatus status) {
        return leaveRequestRepository.findByStatus(status);
    }

    /**
     * Retrieves leave requests by status with pagination
     * @param status Leave request status
     * @param pageable Pagination information
     * @return Page of leave requests with the status
     */
    public Page<LeaveRequest> getLeaveRequestsByStatus(LeaveStatus status, Pageable pageable) {
        return leaveRequestRepository.findByStatus(status, pageable);
    }

    /**
     * Retrieves leave requests for a specific employee by status
     * @param employeeId Employee ID
     * @param status Leave request status
     * @return List of leave requests for the employee with the status
     */
    public List<LeaveRequest> getEmployeeLeaveRequestsByStatus(Long employeeId, LeaveStatus status) {
        // Verify employee exists
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        return leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, status);
    }

    /**
     * Retrieves leave requests for a specific department
     * @param departmentId Department ID
     * @return List of leave requests for employees in the department
     */
    public List<LeaveRequest> getDepartmentLeaveRequests(Long departmentId) {
        return leaveRequestRepository.findByDepartmentId(departmentId);
    }

    /**
     * Retrieves leave requests for a specific department by status
     * @param departmentId Department ID
     * @param status Leave request status
     * @return List of leave requests for employees in the department with the status
     */
    public List<LeaveRequest> getDepartmentLeaveRequestsByStatus(Long departmentId, LeaveStatus status) {
        return leaveRequestRepository.findByDepartmentIdAndStatus(departmentId, status);
    }

    /**
     * Retrieves emergency leave requests
     * @return List of emergency leave requests
     */
    public List<LeaveRequest> getEmergencyLeaveRequests() {
        return leaveRequestRepository.findByIsEmergencyTrue();
    }

    /**
     * Retrieves leave requests requiring documentation
     * @return List of leave requests requiring documentation
     */
    public List<LeaveRequest> getRequestsRequiringDocumentation() {
        return leaveRequestRepository.findRequestsRequiringDocumentation();
    }

    /**
     * Retrieves leave requests submitted in the last N days
     * @param days Number of days to look back
     * @return List of leave requests submitted in the last N days
     */
    public List<LeaveRequest> getRecentLeaveRequests(int days) {
        LocalDateTime date = LocalDateTime.now().minusDays(days);
        return leaveRequestRepository.findRequestsSubmittedInLastNDays(date);
    }

    /**
     * Retrieves leave requests with long duration
     * @param days Minimum duration in days
     * @return List of leave requests with long duration
     */
    public List<LeaveRequest> getLongDurationRequests(int days) {
        return leaveRequestRepository.findLongDurationRequests(days);
    }

    /**
     * Counts leave requests by status
     * @return Map of status to count
     */
    public Map<LeaveStatus, Long> countLeaveRequestsByStatus() {
        return leaveRequestRepository.findAll().stream()
                .collect(Collectors.groupingBy(LeaveRequest::getStatus, Collectors.counting()));
    }

    /**
     * Counts leave requests by leave type
     * @return Map of leave type name to count
     */
    public Map<String, Long> countLeaveRequestsByType() {
        return leaveRequestRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        request -> request.getLeaveType().getName(),
                        Collectors.counting()));
    }

    /**
     * Marks a leave request as taken
     * @param id Leave request ID
     * @return The updated leave request
     */
    @Transactional
    public LeaveRequest markAsTaken(Long id) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);

        // Only allow marking APPROVED requests as TAKEN
        if (leaveRequest.getStatus() != LeaveStatus.APPROVED) {
            throw new RuntimeException("Cannot mark a leave request as taken if it is not in APPROVED status");
        }

        // Update status
        leaveRequest.setStatus(LeaveStatus.TAKEN);

        return leaveRequestRepository.save(leaveRequest);
    }

    /**
     * Marks a leave request as not taken
     * @param id Leave request ID
     * @return The updated leave request
     */
    @Transactional
    public LeaveRequest markAsNotTaken(Long id) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);

        // Only allow marking APPROVED requests as NOT_TAKEN
        if (leaveRequest.getStatus() != LeaveStatus.APPROVED) {
            throw new RuntimeException("Cannot mark a leave request as not taken if it is not in APPROVED status");
        }

        // Update status
        leaveRequest.setStatus(LeaveStatus.NOT_TAKEN);

        // If it's a paid leave type, restore the leave balance
        if (leaveRequest.getLeaveType().getPaidLeave()) {
            int year = leaveRequest.getStartDate().getYear();
            int durationInDays = leaveRequest.getDurationInDays();

            leaveBalanceService.updateUsedDays(
                    leaveRequest.getEmployee().getId(),
                    leaveRequest.getLeaveType().getId(),
                    year,
                    -durationInDays);  // Negative value to restore balance
        }

        return leaveRequestRepository.save(leaveRequest);
    }

    /**
     * Requests additional information for a leave request
     * @param id Leave request ID
     * @param managerComment Comment from the manager
     * @return The updated leave request
     */
    @Transactional
    public LeaveRequest requestInformation(Long id, String managerComment) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);

        // Only allow requesting information for PENDING requests
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Cannot request information for a leave request that is not in PENDING status");
        }

        // Update status and manager comment
        leaveRequest.setStatus(LeaveStatus.INFORMATION_REQUIRED);
        leaveRequest.setManagerComment(managerComment);
        leaveRequest.setResponseDate(LocalDateTime.now());

        return leaveRequestRepository.save(leaveRequest);
    }

    /**
     * Updates a leave request attachment
     * @param id Leave request ID
     * @param attachmentPath Path to the attachment
     * @return The updated leave request
     */
    @Transactional
    public LeaveRequest updateAttachment(Long id, String attachmentPath) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);

        // Update attachment path
        leaveRequest.setAttachmentPath(attachmentPath);

        return leaveRequestRepository.save(leaveRequest);
    }
}
