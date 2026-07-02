package com.leavemanagement.leave.controller;

import com.leavemanagement.leave.dto.InitBalanceRequest;
import com.leavemanagement.leave.dto.LeaveBalanceDTO;
import com.leavemanagement.leave.service.LeaveBalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-balances")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveBalanceDTO>> getEmployeeBalances(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveBalanceService.getEmployeeBalances(employeeId));
    }

    @GetMapping("/employee/{employeeId}/year/{year}")
    public ResponseEntity<List<LeaveBalanceDTO>> getBalancesForYear(@PathVariable Long employeeId,
                                                                     @PathVariable int year) {
        return ResponseEntity.ok(leaveBalanceService.getEmployeeBalancesForYear(employeeId, year));
    }

    @GetMapping("/employee/{employeeId}/leave-type/{leaveTypeId}/year/{year}")
    public ResponseEntity<LeaveBalanceDTO> getBalance(@PathVariable Long employeeId,
                                                       @PathVariable Long leaveTypeId,
                                                       @PathVariable int year) {
        return ResponseEntity.ok(leaveBalanceService.getBalance(employeeId, leaveTypeId, year));
    }

    @PostMapping("/initialize")
    public ResponseEntity<LeaveBalanceDTO> initializeBalance(@Valid @RequestBody InitBalanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveBalanceService.initializeBalance(request));
    }
}
