package com.leavemanagement.employee.event;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCreatedEvent {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate hireDate;
}
