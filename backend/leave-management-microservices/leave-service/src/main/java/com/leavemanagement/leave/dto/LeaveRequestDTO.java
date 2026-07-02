package com.leavemanagement.leave.dto;

import com.leavemanagement.leave.entity.LeaveStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequestDTO {
    private Long id;
    private Long employeeId;
    private Long leaveTypeId;
    private String leaveTypeName;
    private String leaveTypeColor;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean halfDayStart;
    private boolean halfDayEnd;
    private String reason;
    private boolean isEmergency;
    private String contactInfo;
    private LeaveStatus status;
    private String statusDisplay;
    private int numberOfDays;
    private String managerComment;
    private LocalDateTime createdAt;
    private LocalDateTime responseDate;
}
