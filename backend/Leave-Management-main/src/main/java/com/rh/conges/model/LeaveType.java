package com.rh.conges.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a type of leave that employees can request
 */
@Entity
@Table(name = "leave_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the leave type (e.g., Annual Leave, Sick Leave)
     */
    @NotBlank(message = "Leave type name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Detailed description of the leave type
     */
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(length = 500)
    private String description;

    /**
     * Indicates if this is a paid leave type
     */
    @Column(name = "paid_leave", nullable = false)
    @NotNull(message = "Paid leave status is required")
    private Boolean paidLeave = true;

    /**
     * Default number of days allocated per year
     */
    @Column(name = "default_days_per_year", nullable = false)
    @NotNull(message = "Default days per year is required")
    @Min(value = 0, message = "Default days cannot be negative")
    private Integer defaultDaysPerYear;

    /**
     * Color code for UI display (e.g., in calendars)
     */
    @Column(name = "color_code", length = 7)
    @Size(min = 7, max = 7, message = "Color code must be in format #RRGGBB")
    private String colorCode;

    /**
     * Indicates if this leave type requires documentation (e.g., medical certificate)
     */
    @Column(name = "requires_documentation")
    private Boolean requiresDocumentation = false;

    /**
     * Indicates if this leave type can be carried over to next year
     */
    @Column(name = "can_carry_over")
    private Boolean canCarryOver = false;

    /**
     * Maximum days that can be carried over to next year
     */
    @Column(name = "max_carry_over_days")
    @Min(value = 0, message = "Max carry over days cannot be negative")
    private Integer maxCarryOverDays = 0;

    /**
     * Minimum advance notice required (in days)
     */
    @Column(name = "min_notice_days")
    @Min(value = 0, message = "Minimum notice days cannot be negative")
    private Integer minNoticeDays = 0;

    /**
     * Maximum consecutive days allowed
     */
    @Column(name = "max_consecutive_days")
    @Min(value = 0, message = "Maximum consecutive days cannot be negative")
    private Integer maxConsecutiveDays;

    /**
     * Indicates if half-day requests are allowed
     */
    @Column(name = "allow_half_day")
    private Boolean allowHalfDay = true;

    /**
     * Indicates if this leave type is active and can be requested
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Leave balances associated with this leave type
     */
    @OneToMany(mappedBy = "leaveType")
    @JsonIgnore
    private List<LeaveBalance> leaveBalances = new ArrayList<>();

    /**
     * Leave requests of this type
     */
    @OneToMany(mappedBy = "leaveType")
    @JsonIgnore
    private List<LeaveRequest> leaveRequests = new ArrayList<>();

    /**
     * Checks if this leave type is available for new requests
     * @return true if the leave type is active, false otherwise
     */
    @Transient
    public boolean isAvailable() {
        return active;
    }

    /**
     * Checks if documentation is required for this leave type
     * @return true if documentation is required, false otherwise
     */
    @Transient
    public boolean needsDocumentation() {
        return requiresDocumentation != null && requiresDocumentation;
    }

    /**
     * Checks if this leave type allows carry over to next year
     * @return true if carry over is allowed, false otherwise
     */
    @Transient
    public boolean allowsCarryOver() {
        return canCarryOver != null && canCarryOver;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPaidLeave() {
        return paidLeave;
    }

    public boolean isPaidLeave() {
        return paidLeave != null && paidLeave;
    }

    public void setPaidLeave(Boolean paidLeave) {
        this.paidLeave = paidLeave;
    }

    public Integer getDefaultDaysPerYear() {
        return defaultDaysPerYear;
    }

    public void setDefaultDaysPerYear(Integer defaultDaysPerYear) {
        this.defaultDaysPerYear = defaultDaysPerYear;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Boolean getRequiresDocumentation() {
        return requiresDocumentation;
    }

    public void setRequiresDocumentation(Boolean requiresDocumentation) {
        this.requiresDocumentation = requiresDocumentation;
    }

    public Boolean getCanCarryOver() {
        return canCarryOver;
    }

    public void setCanCarryOver(Boolean canCarryOver) {
        this.canCarryOver = canCarryOver;
    }

    public Integer getMaxCarryOverDays() {
        return maxCarryOverDays;
    }

    public void setMaxCarryOverDays(Integer maxCarryOverDays) {
        this.maxCarryOverDays = maxCarryOverDays;
    }

    public Integer getMinNoticeDays() {
        return minNoticeDays;
    }

    public void setMinNoticeDays(Integer minNoticeDays) {
        this.minNoticeDays = minNoticeDays;
    }

    public Integer getMaxConsecutiveDays() {
        return maxConsecutiveDays;
    }

    public void setMaxConsecutiveDays(Integer maxConsecutiveDays) {
        this.maxConsecutiveDays = maxConsecutiveDays;
    }

    public Boolean getAllowHalfDay() {
        return allowHalfDay;
    }

    public void setAllowHalfDay(Boolean allowHalfDay) {
        this.allowHalfDay = allowHalfDay;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<LeaveBalance> getLeaveBalances() {
        return leaveBalances;
    }

    public void setLeaveBalances(List<LeaveBalance> leaveBalances) {
        this.leaveBalances = leaveBalances;
    }

    public List<LeaveRequest> getLeaveRequests() {
        return leaveRequests;
    }

    public void setLeaveRequests(List<LeaveRequest> leaveRequests) {
        this.leaveRequests = leaveRequests;
    }
}
