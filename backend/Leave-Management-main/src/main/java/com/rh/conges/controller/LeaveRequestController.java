package com.rh.conges.controller;

import com.rh.conges.model.LeaveRequest;
import com.rh.conges.model.LeaveStatus;
import com.rh.conges.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing leave requests
 */
@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    /**
     * Get all leave requests
     * @return List of all leave requests
     */
    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        List<LeaveRequest> leaveRequests = leaveRequestService.getAllLeaveRequests();
        return new ResponseEntity<>(leaveRequests, HttpStatus.OK);
    }

    /**
     * Get leave request by ID
     * @param id Leave request ID
     * @return The leave request
     */
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> getLeaveRequestById(@PathVariable Long id) {
        try {
            LeaveRequest leaveRequest = leaveRequestService.getLeaveRequestById(id);
            return new ResponseEntity<>(leaveRequest, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get leave requests for a specific employee
     * @param employeeId Employee ID
     * @return List of leave requests for the employee
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequest>> getEmployeeLeaveRequests(@PathVariable Long employeeId) {
        List<LeaveRequest> leaveRequests = leaveRequestService.getEmployeeLeaveRequests(employeeId);
        return new ResponseEntity<>(leaveRequests, HttpStatus.OK);
    }

    /**
     * Get leave requests for employees managed by a specific manager
     * @param managerId Manager ID
     * @return List of leave requests for the team
     */
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<LeaveRequest>> getTeamLeaveRequests(@PathVariable Long managerId) {
        List<LeaveRequest> leaveRequests = leaveRequestService.getTeamLeaveRequests(managerId);
        return new ResponseEntity<>(leaveRequests, HttpStatus.OK);
    }

    /**
     * Get pending leave requests for a manager's team
     * @param managerId Manager ID
     * @return List of pending leave requests
     */
    @GetMapping("/manager/{managerId}/pending")
    public ResponseEntity<List<LeaveRequest>> getPendingTeamLeaveRequests(@PathVariable Long managerId) {
        List<LeaveRequest> leaveRequests = leaveRequestService.getPendingTeamLeaveRequests(managerId);
        return new ResponseEntity<>(leaveRequests, HttpStatus.OK);
    }

    /**
     * Get leave requests by status
     * @param status Leave request status
     * @return List of leave requests with the given status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByStatus(@PathVariable LeaveStatus status) {
        List<LeaveRequest> leaveRequests = leaveRequestService.getLeaveRequestsByStatus(status);
        return new ResponseEntity<>(leaveRequests, HttpStatus.OK);
    }

    /**
     * Get leave requests within a date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of leave requests in the date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LeaveRequest> leaveRequests = leaveRequestService.getLeaveRequestsByDateRange(startDate, endDate);
        return new ResponseEntity<>(leaveRequests, HttpStatus.OK);
    }

    /**
     * Get leave requests for a specific department
     * @param departmentId Department ID
     * @return List of leave requests for the department
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<LeaveRequest>> getDepartmentLeaveRequests(@PathVariable Long departmentId) {
        List<LeaveRequest> leaveRequests = leaveRequestService.getDepartmentLeaveRequests(departmentId);
        return new ResponseEntity<>(leaveRequests, HttpStatus.OK);
    }

    /**
     * Create a new leave request
     * @param body DTO with employeeId, leaveTypeId, startDate, endDate, etc.
     * @return The created leave request
     */
    @PostMapping
    public ResponseEntity<?> createLeaveRequest(@RequestBody Map<String, Object> body) {
        try {
            LeaveRequest leaveRequest = new LeaveRequest();

            com.rh.conges.model.Employee emp = new com.rh.conges.model.Employee();
            emp.setId(Long.valueOf(body.get("employeeId").toString()));
            leaveRequest.setEmployee(emp);

            com.rh.conges.model.LeaveType lt = new com.rh.conges.model.LeaveType();
            lt.setId(Long.valueOf(body.get("leaveTypeId").toString()));
            leaveRequest.setLeaveType(lt);

            leaveRequest.setStartDate(java.time.LocalDate.parse(body.get("startDate").toString()));
            leaveRequest.setEndDate(java.time.LocalDate.parse(body.get("endDate").toString()));

            if (body.get("reason") != null)
                leaveRequest.setReason(body.get("reason").toString());
            if (body.get("contactInfo") != null)
                leaveRequest.setContactInfo(body.get("contactInfo").toString());
            if (body.get("halfDayStart") != null)
                leaveRequest.setHalfDayStart(Boolean.valueOf(body.get("halfDayStart").toString()));
            if (body.get("halfDayEnd") != null)
                leaveRequest.setHalfDayEnd(Boolean.valueOf(body.get("halfDayEnd").toString()));
            if (body.get("isEmergency") != null)
                leaveRequest.setIsEmergency(Boolean.valueOf(body.get("isEmergency").toString()));

            LeaveRequest createdRequest = leaveRequestService.createLeaveRequest(leaveRequest);
            return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Approve a leave request
     * @param id Leave request ID
     * @param body Request body with managerComment (optional)
     * @return The approved leave request
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveRequest> approveLeaveRequest(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String managerComment = (body != null) ? body.get("managerComment") : null;
            LeaveRequest approvedRequest = leaveRequestService.approveLeaveRequest(id, managerComment);
            return new ResponseEntity<>(approvedRequest, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Reject a leave request
     * @param id Leave request ID
     * @param body Request body with managerComment (required)
     * @return The rejected leave request
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<LeaveRequest> rejectLeaveRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String managerComment = body.get("managerComment");
            if (managerComment == null || managerComment.trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            LeaveRequest rejectedRequest = leaveRequestService.rejectLeaveRequest(id, managerComment);
            return new ResponseEntity<>(rejectedRequest, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Cancel a leave request
     * @param id Leave request ID
     * @return The cancelled leave request
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<LeaveRequest> cancelLeaveRequest(@PathVariable Long id) {
        try {
            LeaveRequest cancelledRequest = leaveRequestService.cancelLeaveRequest(id);
            return new ResponseEntity<>(cancelledRequest, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Request more information for a leave request
     * @param id Leave request ID
     * @param body Request body with managerComment (required)
     * @return The updated leave request
     */
    @PutMapping("/{id}/request-information")
    public ResponseEntity<LeaveRequest> requestInformation(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String managerComment = body.get("managerComment");
            if (managerComment == null || managerComment.trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            LeaveRequest updatedRequest = leaveRequestService.requestInformation(id, managerComment);
            return new ResponseEntity<>(updatedRequest, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

