package com.leavemanagement.leave.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "leave_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(unique = true, nullable = false)
    private String name;

    @Size(max = 500)
    private String description;

    @Builder.Default
    private boolean paidLeave = true;

    @Min(0)
    private int defaultDaysPerYear;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Format couleur invalide (ex: #FF5733)")
    private String colorCode;

    @Builder.Default
    private boolean requiresDocumentation = false;

    @Builder.Default
    private boolean canCarryOver = false;

    @Min(0)
    @Builder.Default
    private int maxCarryOverDays = 0;

    @Min(0)
    @Builder.Default
    private int minNoticeDays = 0;

    @Builder.Default
    private boolean allowHalfDay = true;

    @Builder.Default
    private boolean active = true;
}
