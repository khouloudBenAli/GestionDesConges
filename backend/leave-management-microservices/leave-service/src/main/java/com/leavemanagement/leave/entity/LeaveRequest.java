package com.leavemanagement.leave.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Référence vers employee-service (pas de FK entre DBs différentes)
    @Column(nullable = false)
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @Builder.Default
    private boolean halfDayStart = false;

    @Builder.Default
    private boolean halfDayEnd = false;

    @Size(max = 500)
    private String reason;

    @Builder.Default
    private boolean isEmergency = false;

    @Size(max = 200)
    private String contactInfo;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LeaveStatus status = LeaveStatus.PENDING;

    private int numberOfDays;

    @Size(max = 500)
    private String managerComment;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime responseDate;

    @PrePersist
    @PreUpdate
    public void calculateDays() {
        if (startDate != null && endDate != null) {
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            if (halfDayStart) days -= 0.5;
            if (halfDayEnd) days -= 0.5;
            this.numberOfDays = (int) Math.max(1, days);
        }
    }

    public boolean isValid() {
        return startDate != null && endDate != null && !endDate.isBefore(startDate);
    }
}
