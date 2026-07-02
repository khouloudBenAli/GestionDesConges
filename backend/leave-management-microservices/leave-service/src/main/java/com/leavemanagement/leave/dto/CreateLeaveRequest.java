package com.leavemanagement.leave.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLeaveRequest {

    @NotNull(message = "L'identifiant de l'employé est obligatoire")
    private Long employeeId;

    @NotNull(message = "Le type de congé est obligatoire")
    private Long leaveTypeId;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate endDate;

    private boolean halfDayStart = false;
    private boolean halfDayEnd = false;

    @Size(max = 500)
    private String reason;

    private boolean isEmergency = false;

    @Size(max = 200)
    private String contactInfo;
}
