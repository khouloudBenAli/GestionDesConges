package com.rh.conges.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Entity representing a leave request submitted by an employee
 */
@Entity
@Table(name = "leave_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The employee who submitted this leave request
     */
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull(message = "Employee is required")
    private Employee employee;

    /**
     * The type of leave requested
     */
    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;

    /**
     * The start date of the leave
     */
    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    /**
     * The end date of the leave
     */
    @Column(name = "end_date", nullable = false)
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    /**
     * Half day indicator for start date (morning or afternoon)
     */
    @Column(name = "half_day_start")
    private Boolean halfDayStart = false;

    /**
     * Half day indicator for end date (morning or afternoon)
     */
    @Column(name = "half_day_end")
    private Boolean halfDayEnd = false;

    /**
     * The reason for requesting leave
     */
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    @Column(length = 500)
    private String reason;

    /**
     * Current status of the leave request
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status is required")
    private LeaveStatus status = LeaveStatus.PENDING;

    /**
     * Comment provided by the manager when approving/rejecting
     */
    @Size(max = 500, message = "Manager comment cannot exceed 500 characters")
    @Column(name = "manager_comment", length = 500)
    private String managerComment;

    /**
     * Date and time when the request was submitted
     */
    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    /**
     * Date and time when the manager responded to the request
     */
    @Column(name = "response_date")
    private LocalDateTime responseDate;

    /**
     * Document attachment (e.g., medical certificate)
     */
    @Column(name = "attachment_path")
    private String attachmentPath;

    /**
     * Flag indicating if this is an emergency leave
     */
    @Column(name = "is_emergency")
    private Boolean isEmergency = false;

    /**
     * Contact information during leave
     */
    @Column(name = "contact_info")
    private String contactInfo;

    /**
     * Calculates the number of days requested
     * @return Number of days between start and end dates (inclusive)
     */
    @Transient
    public int getDurationInDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }

        // Basic calculation (including weekends and holidays)
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Adjust for half days
        if (halfDayStart != null && halfDayStart) {
            days -= 0.5;
        }
        if (halfDayEnd != null && halfDayEnd) {
            days -= 0.5;
        }

        return (int) days;
    }

    /**
     * Sets default values before persisting
     */
    @PrePersist
    protected void onCreate() {
        if (requestDate == null) {
            requestDate = LocalDateTime.now();
        }
        if (status == null) {
            status = LeaveStatus.PENDING;
        }
    }

    /**
     * Validates the leave request
     * @return true if valid, false otherwise
     */
    @Transient
    public boolean isValid() {
        if (startDate == null || endDate == null) {
            return false;
        }

        // Start date must be before or equal to end date
        return !startDate.isAfter(endDate);
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getHalfDayStart() {
        return halfDayStart;
    }

    public void setHalfDayStart(Boolean halfDayStart) {
        this.halfDayStart = halfDayStart;
    }

    public Boolean getHalfDayEnd() {
        return halfDayEnd;
    }

    public void setHalfDayEnd(Boolean halfDayEnd) {
        this.halfDayEnd = halfDayEnd;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public String getManagerComment() {
        return managerComment;
    }

    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public Boolean getIsEmergency() {
        return isEmergency;
    }

    public void setIsEmergency(Boolean isEmergency) {
        this.isEmergency = isEmergency;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
