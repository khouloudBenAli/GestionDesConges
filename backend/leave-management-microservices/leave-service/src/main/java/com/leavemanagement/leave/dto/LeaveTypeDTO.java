package com.leavemanagement.leave.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveTypeDTO {
    private Long id;
    private String name;
    private String description;
    private boolean paidLeave;
    private int defaultDaysPerYear;
    private String colorCode;
    private boolean requiresDocumentation;
    private boolean canCarryOver;
    private int maxCarryOverDays;
    private int minNoticeDays;
    private boolean allowHalfDay;
    private boolean active;
}
