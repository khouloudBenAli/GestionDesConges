package com.leavemanagement.leave.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InitBalanceRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private Long leaveTypeId;

    @NotNull
    @Min(2020)
    private Integer year;

    @Min(0)
    private int allocatedDays;
}
