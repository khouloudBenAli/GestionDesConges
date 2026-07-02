package com.leavemanagement.leave.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalanceDTO {
    private Long id;
    private Long employeeId;
    private Long leaveTypeId;
    private String leaveTypeName;
    private String leaveTypeColor;
    private Integer year;
    private int totalDays;
    private int allocatedDays;
    private int usedDays;
    private int remainingDays;
    private int carriedOverDays;
    private LocalDateTime lastUpdated;
}
