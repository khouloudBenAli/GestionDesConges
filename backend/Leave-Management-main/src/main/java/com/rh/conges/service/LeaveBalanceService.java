package com.rh.conges.service;

import com.rh.conges.model.Employee;
import com.rh.conges.model.LeaveBalance;
import com.rh.conges.model.LeaveType;
import com.rh.conges.repository.EmployeeRepository;
import com.rh.conges.repository.LeaveBalanceRepository;
import com.rh.conges.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing employee leave balances
 */
@Service
public class LeaveBalanceService {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    /**
     * Retrieves all leave balances
     * @return List of all leave balances
     */
    public List<LeaveBalance> getAllLeaveBalances() {
        return leaveBalanceRepository.findAll();
    }

    /**
     * Retrieves a leave balance by ID
     * @param id Leave balance ID
     * @return The leave balance if found
     * @throws RuntimeException if leave balance not found
     */
    public LeaveBalance getLeaveBalanceById(Long id) {
        return leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave balance not found with ID: " + id));
    }

    /**
     * Retrieves leave balances for a specific employee
     * @param employeeId Employee ID
     * @return List of leave balances for the employee
     */
    public List<LeaveBalance> getEmployeeLeaveBalances(Long employeeId) {
        // Verify employee exists
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        return leaveBalanceRepository.findByEmployeeId(employeeId);
    }

    /**
     * Retrieves leave balances for a specific employee in a specific year
     * @param employeeId Employee ID
     * @param year Year
     * @return List of leave balances for the employee in the year
     */
    public List<LeaveBalance> getEmployeeLeaveBalancesForYear(Long employeeId, int year) {
        // Verify employee exists
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        return leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, year);
    }

    /**
     * Retrieves a specific leave balance by employee, leave type, and year
     * @param employeeId Employee ID
     * @param leaveTypeId Leave type ID
     * @param year Year
     * @return The leave balance if found
     * @throws RuntimeException if leave balance not found
     */
    public LeaveBalance getLeaveBalance(Long employeeId, Long leaveTypeId, int year) {
        return leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
                .orElseThrow(() -> new RuntimeException(
                        "Leave balance not found for employee ID: " + employeeId +
                                ", leave type ID: " + leaveTypeId +
                                ", year: " + year));
    }

    /**
     * Initializes a leave balance for an employee
     * @param employeeId Employee ID
     * @param leaveTypeId Leave type ID
     * @param year Year
     * @return The initialized leave balance
     */
    @Transactional
    public LeaveBalance initializeLeaveBalance(Long employeeId, Long leaveTypeId, int year) {
        // Check if balance already exists
        Optional<LeaveBalance> existingBalance =
                leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year);

        if (existingBalance.isPresent()) {
            throw new RuntimeException("Leave balance already exists for this employee, leave type, and year");
        }

        // Get employee and leave type
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found with ID: " + leaveTypeId));

        // Create new balance
        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setEmployee(employee);
        leaveBalance.setLeaveType(leaveType);
        leaveBalance.setYear(year);
        leaveBalance.setTotalDays(leaveType.getDefaultDaysPerYear());
        leaveBalance.setUsedDays(0);
        leaveBalance.setCarriedOverDays(0);
        leaveBalance.setAdditionalDays(0);
        leaveBalance.setLastUpdated(LocalDateTime.now());

        return leaveBalanceRepository.save(leaveBalance);
    }

    /**
     * Updates a leave balance
     * @param leaveBalance Leave balance to update
     * @return The updated leave balance
     */
    @Transactional
    public LeaveBalance updateLeaveBalance(LeaveBalance leaveBalance) {
        // Verify leave balance exists
        getLeaveBalanceById(leaveBalance.getId());

        // Update last updated timestamp
        leaveBalance.setLastUpdated(LocalDateTime.now());

        return leaveBalanceRepository.save(leaveBalance);
    }

    /**
     * Updates used days for a leave balance
     * @param employeeId Employee ID
     * @param leaveTypeId Leave type ID
     * @param year Year
     * @param daysToAdd Number of days to add to used days
     * @return The updated leave balance
     */
    @Transactional
    public LeaveBalance updateUsedDays(Long employeeId, Long leaveTypeId, int year, int daysToAdd) {
        LeaveBalance leaveBalance = getLeaveBalance(employeeId, leaveTypeId, year);

        // Update used days
        leaveBalance.setUsedDays(leaveBalance.getUsedDays() + daysToAdd);
        leaveBalance.setLastUpdated(LocalDateTime.now());

        return leaveBalanceRepository.save(leaveBalance);
    }

    /**
     * Adds additional days to a leave balance
     * @param employeeId Employee ID
     * @param leaveTypeId Leave type ID
     * @param year Year
     * @param additionalDays Number of additional days to add
     * @param reason Reason for adding additional days
     * @return The updated leave balance
     */
    @Transactional
    public LeaveBalance addAdditionalDays(Long employeeId, Long leaveTypeId, int year, int additionalDays, String reason) {
        if (additionalDays <= 0) {
            throw new IllegalArgumentException("Additional days must be greater than zero");
        }

        LeaveBalance leaveBalance = getLeaveBalance(employeeId, leaveTypeId, year);

        // Update additional days
        leaveBalance.setAdditionalDays(leaveBalance.getAdditionalDays() + additionalDays);
        leaveBalance.setLastUpdated(LocalDateTime.now());

        // Add reason to notes
        String notes = leaveBalance.getNotes();
        String newNote = LocalDate.now() + ": Added " + additionalDays + " additional days. Reason: " + reason;

        if (notes == null || notes.isEmpty()) {
            leaveBalance.setNotes(newNote);
        } else {
            leaveBalance.setNotes(notes + "\n" + newNote);
        }

        return leaveBalanceRepository.save(leaveBalance);
    }

    /**
     * Initializes leave balances for all employees for a specific year
     * @param year Year
     */
    @Transactional
    public void initializeYearlyBalances(int year) {
        // Get all active employees
        List<Employee> employees = employeeRepository.findAll();

        // Get all active leave types
        List<LeaveType> leaveTypes = leaveTypeRepository.findByActiveTrue();

        // For each employee and leave type, initialize balance if it doesn't exist
        for (Employee employee : employees) {
            for (LeaveType leaveType : leaveTypes) {
                if (!leaveBalanceRepository.existsByEmployeeIdAndLeaveTypeIdAndYear(
                        employee.getId(), leaveType.getId(), year)) {

                    // Check for previous year balance for carry over
                    int carryOverDays = 0;
                    if (leaveType.getCanCarryOver()) {
                        Optional<LeaveBalance> previousYearBalance =
                                leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                                        employee.getId(), leaveType.getId(), year - 1);

                        if (previousYearBalance.isPresent()) {
                            int remainingDays = previousYearBalance.get().getRemainingDays();
                            int maxCarryOver = leaveType.getMaxCarryOverDays() != null ?
                                    leaveType.getMaxCarryOverDays() : 0;

                            carryOverDays = Math.min(remainingDays, maxCarryOver);
                        }
                    }

                    // Create new balance
                    LeaveBalance leaveBalance = new LeaveBalance();
                    leaveBalance.setEmployee(employee);
                    leaveBalance.setLeaveType(leaveType);
                    leaveBalance.setYear(year);
                    leaveBalance.setTotalDays(leaveType.getDefaultDaysPerYear());
                    leaveBalance.setUsedDays(0);
                    leaveBalance.setCarriedOverDays(carryOverDays);
                    leaveBalance.setAdditionalDays(0);
                    leaveBalance.setLastUpdated(LocalDateTime.now());

                    if (carryOverDays > 0) {
                        leaveBalance.setNotes("Carried over " + carryOverDays + " days from " + (year - 1));
                    }

                    leaveBalanceRepository.save(leaveBalance);
                }
            }
        }
    }

    /**
     * Checks if an employee has sufficient leave balance for a request
     * @param employeeId Employee ID
     * @param leaveTypeId Leave type ID
     * @param year Year
     * @param requestedDays Number of days requested
     * @return true if balance is sufficient, false otherwise
     */
    public boolean hasSufficientBalance(Long employeeId, Long leaveTypeId, int year, int requestedDays) {
        try {
            LeaveBalance leaveBalance = getLeaveBalance(employeeId, leaveTypeId, year);
            return leaveBalance.getRemainingDays() >= requestedDays;
        } catch (RuntimeException e) {
            // If balance doesn't exist, return false
            return false;
        }
    }

    /**
     * Retrieves leave balances for employees in a department
     * @param departmentId Department ID
     * @param year Year
     * @return List of leave balances for employees in the department
     */
    public List<LeaveBalance> getDepartmentLeaveBalances(Long departmentId, int year) {
        return leaveBalanceRepository.findByDepartmentIdAndYear(departmentId, year);
    }

    /**
     * Retrieves leave balances for employees under a manager
     * @param managerId Manager ID
     * @param year Year
     * @return List of leave balances for employees under the manager
     */
    public List<LeaveBalance> getTeamLeaveBalances(Long managerId, int year) {
        return leaveBalanceRepository.findByManagerIdAndYear(managerId, year);
    }

    /**
     * Generates a summary of leave balances by leave type
     * @param year Year
     * @return Map of leave type names to average remaining days
     */
    public Map<String, Double> getLeaveBalanceSummaryByType(int year) {
        List<LeaveBalance> allBalances = leaveBalanceRepository.findByYear(year);

        return allBalances.stream()
                .collect(Collectors.groupingBy(
                        balance -> balance.getLeaveType().getName(),
                        Collectors.averagingDouble(LeaveBalance::getRemainingDays)
                ));
    }

    /**
     * Retrieves employees with low leave balance
     * @param leaveTypeId Leave type ID
     * @param year Year
     * @param threshold Threshold for low balance
     * @return List of leave balances below the threshold
     */
    public List<LeaveBalance> getEmployeesWithLowBalance(Long leaveTypeId, int year, int threshold) {
        List<LeaveBalance> balances = leaveBalanceRepository.findByLeaveTypeIdAndYear(leaveTypeId, year);

        return balances.stream()
                .filter(balance -> balance.getRemainingDays() <= threshold)
                .collect(Collectors.toList());
    }
}
