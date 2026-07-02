package com.rh.conges.controller;

import com.rh.conges.dto.LeaveBalanceInitRequest;
import com.rh.conges.model.LeaveBalance;
import com.rh.conges.service.LeaveBalanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave-balances")
public class LeaveBalanceController {

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    /**
     * Retrieves leave balances for an employee
     * @param employeeId Employee ID
     * @return List of leave balances
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveBalance>> getEmployeeLeaveBalances(@PathVariable Long employeeId) {
        List<LeaveBalance> leaveBalances = leaveBalanceService.getEmployeeLeaveBalances(employeeId);
        return new ResponseEntity<>(leaveBalances, HttpStatus.OK);
    }

    /**
     * Retrieves a specific leave balance by employee, leave type and year
     * @param employeeId Employee ID
     * @param leaveTypeId Leave type ID
     * @param year Year for the balance
     * @return The leave balance
     */
    @GetMapping
    public ResponseEntity<LeaveBalance> getLeaveBalance(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestParam int year) {
        try {
            LeaveBalance leaveBalance = leaveBalanceService.getLeaveBalance(employeeId, leaveTypeId, year);
            return new ResponseEntity<>(leaveBalance, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Initializes a new leave balance for an employee
     * @param request Leave balance initialization request with employeeId, leaveTypeId, and year
     * @return The created leave balance
     */
    @PostMapping("/initialize")
    public ResponseEntity<LeaveBalance> initializeLeaveBalance(@Valid @RequestBody LeaveBalanceInitRequest request) {
        // If year is not provided, use current year
        int balanceYear = (request.getYear() != null) ? request.getYear() : LocalDate.now().getYear();

        try {
            LeaveBalance leaveBalance = leaveBalanceService.initializeLeaveBalance(
                    request.getEmployeeId(),
                    request.getLeaveTypeId(),
                    balanceYear
            );
            return new ResponseEntity<>(leaveBalance, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updates an existing leave balance
     * @param id Leave balance ID
     * @param leaveBalance New leave balance data
     * @return The updated leave balance
     */
    @PutMapping("/{id}")
    public ResponseEntity<LeaveBalance> updateLeaveBalance(
            @PathVariable Long id,
            @RequestBody LeaveBalance leaveBalance) {

        // Ensure the ID in the path matches the ID in the body
        leaveBalance.setId(id);

        try {
            LeaveBalance updatedBalance = leaveBalanceService.updateLeaveBalance(leaveBalance);
            return new ResponseEntity<>(updatedBalance, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Initializes yearly balances for all employees
     * @param year Year to initialize balances for
     * @return Success response
     */
    @PostMapping("/initialize-yearly")
    public ResponseEntity<String> initializeYearlyBalances(@RequestParam(required = false) Integer year) {
        // If year is not provided, use current year
        int balanceYear = (year != null) ? year : LocalDate.now().getYear();

        try {
            leaveBalanceService.initializeYearlyBalances(balanceYear);
            return new ResponseEntity<>("Yearly leave balances initialized successfully for " + balanceYear, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Failed to initialize yearly leave balances: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Checks if an employee has enough leave balance for a request
     * @param employeeId Employee ID
     * @param leaveTypeId Leave type ID
     * @param startDate Start date of leave
     * @param endDate End date of leave
     * @return Boolean indicating if balance is sufficient
     */
    @GetMapping("/check-balance")
    public ResponseEntity<Boolean> checkLeaveBalance(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        try {
            // Create a mock leave request to check balance
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // This would need to be implemented in the service
            // For now, we'll return a placeholder
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
