package com.leavemanagement.leave.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypeRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private boolean paidLeave = true;

    @Min(value = 0, message = "Le nombre de jours doit être positif")
    private int defaultDaysPerYear;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Format couleur invalide (ex: #FF5733)")
    private String colorCode;

    private boolean requiresDocumentation = false;
    private boolean canCarryOver = false;

    @Min(0)
    private int maxCarryOverDays = 0;

    @Min(0)
    private int minNoticeDays = 0;

    private boolean allowHalfDay = true;
    private boolean active = true;
}
