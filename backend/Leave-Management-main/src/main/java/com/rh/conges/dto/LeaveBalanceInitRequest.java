package com.rh.conges.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for initializing a leave balance
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceInitRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Leave Type ID is required")
    private Long leaveTypeId;

    @Min(value = 2000, message = "Year must be valid")
    private Integer year;

    // Getters and Setters (Lombok @Data will generate these, but explicit for clarity)
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(Long leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
