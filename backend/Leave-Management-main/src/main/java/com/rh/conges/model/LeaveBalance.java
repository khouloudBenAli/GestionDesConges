package com.rh.conges.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing an employee's leave balance for a specific leave type and year
 */
@Entity
@Table(name = "leave_balances", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "leave_type_id", "year"},
                name = "uk_employee_leave_type_year")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The employee this leave balance belongs to
     */
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull(message = "Employee is required")
    private Employee employee;

    /**
     * The type of leave this balance is for
     */
    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;

    /**
     * The year this leave balance applies to
     */
    @Column(nullable = false)
    @NotNull(message = "Year is required")
    private Integer year;

    /**
     * Total days allocated for this leave type in the year
     */
    @Column(name = "total_days", nullable = false)
    @NotNull(message = "Total days is required")
    @Min(value = 0, message = "Total days cannot be negative")
    private Integer totalDays;

    /**
     * Days already used from this leave balance
     */
    @Column(name = "used_days", nullable = false)
    @NotNull(message = "Used days is required")
    @Min(value = 0, message = "Used days cannot be negative")
    private Integer usedDays = 0;

    /**
     * Days carried over from previous year
     */
    @Column(name = "carried_over_days")
    @Min(value = 0, message = "Carried over days cannot be negative")
    private Integer carriedOverDays = 0;

    /**
     * Additional days granted (e.g., bonus leave)
     */
    @Column(name = "additional_days")
    @Min(value = 0, message = "Additional days cannot be negative")
    private Integer additionalDays = 0;

    /**
     * Date when this balance was last updated
     */
    @Column(name = "last_updated")
    private java.time.LocalDateTime lastUpdated;

    /**
     * Notes or comments about this leave balance
     */
    @Column(length = 500)
    private String notes;

    /**
     * Calculates the remaining leave balance
     * @return Number of days remaining
     */
    @Transient
    public int getRemainingDays() {
        int total = totalDays + (carriedOverDays != null ? carriedOverDays : 0) +
                (additionalDays != null ? additionalDays : 0);
        return total - usedDays;
    }

    /**
     * Updates the last updated timestamp
     */
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = java.time.LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public Integer getUsedDays() {
        return usedDays;
    }

    public void setUsedDays(Integer usedDays) {
        this.usedDays = usedDays;
    }

    public Integer getCarriedOverDays() {
        return carriedOverDays;
    }

    public void setCarriedOverDays(Integer carriedOverDays) {
        this.carriedOverDays = carriedOverDays;
    }

    public Integer getAdditionalDays() {
        return additionalDays;
    }

    public void setAdditionalDays(Integer additionalDays) {
        this.additionalDays = additionalDays;
    }

    public java.time.LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(java.time.LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
