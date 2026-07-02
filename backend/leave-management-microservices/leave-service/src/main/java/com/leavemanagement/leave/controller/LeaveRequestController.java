package com.leavemanagement.leave.controller;

import com.leavemanagement.leave.dto.CreateLeaveRequest;
import com.leavemanagement.leave.dto.LeaveActionRequest;
import com.leavemanagement.leave.dto.LeaveRequestDTO;
import com.leavemanagement.leave.entity.LeaveStatus;
import com.leavemanagement.leave.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @GetMapping
    public ResponseEntity<List<LeaveRequestDTO>> getAllRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestService.getRequestById(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequestDTO>> getRequestsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveRequestService.getRequestsByEmployee(employeeId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LeaveRequestDTO>> getRequestsByStatus(@PathVariable LeaveStatus status) {
        return ResponseEntity.ok(leaveRequestService.getRequestsByStatus(status));
    }

    @PostMapping
    public ResponseEntity<LeaveRequestDTO> createRequest(@Valid @RequestBody CreateLeaveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveRequestService.createRequest(request));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveRequestDTO> approveRequest(@PathVariable Long id,
                                                          @RequestBody LeaveActionRequest actionRequest) {
        return ResponseEntity.ok(leaveRequestService.approveRequest(id, actionRequest));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<LeaveRequestDTO> rejectRequest(@PathVariable Long id,
                                                         @RequestBody LeaveActionRequest actionRequest) {
        return ResponseEntity.ok(leaveRequestService.rejectRequest(id, actionRequest));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<LeaveRequestDTO> cancelRequest(@PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestService.cancelRequest(id));
    }
}
