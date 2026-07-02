package com.leavemanagement.employee.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDTO {
    private Long id;
    private String name;
    private String description;
    private int employeeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
