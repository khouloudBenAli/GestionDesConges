package com.leavemanagement.leave.event;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveStatusChangedEvent {
    private Long leaveRequestId;
    private Long employeeId;
    private String newStatus;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfDays;
}
