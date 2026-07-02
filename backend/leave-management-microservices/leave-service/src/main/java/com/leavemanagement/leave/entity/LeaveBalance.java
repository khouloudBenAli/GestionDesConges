package com.leavemanagement.leave.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_balances",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "leave_type_id", "year"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Référence vers employee-service
    @Column(nullable = false)
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @NotNull
    private Integer year;

    @Min(0)
    @Builder.Default
    private int totalDays = 0;

    @Min(0)
    @Builder.Default
    private int allocatedDays = 0;

    @Min(0)
    @Builder.Default
    private int usedDays = 0;

    @Min(0)
    @Builder.Default
    private int carriedOverDays = 0;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    public int getRemainingDays() {
        return allocatedDays + carriedOverDays - usedDays;
    }

    public boolean hasSufficientBalance(int requestedDays) {
        return getRemainingDays() >= requestedDays;
    }
}
