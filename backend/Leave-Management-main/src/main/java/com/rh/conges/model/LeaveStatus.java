package com.rh.conges.model;

/**
 * Enumeration representing the possible statuses of a leave request
 */
public enum LeaveStatus {
    /**
     * The leave request has been submitted but not yet reviewed
     */
    PENDING("Pending"),

    /**
     * The leave request has been approved by the manager
     */
    APPROVED("Approved"),

    /**
     * The leave request has been rejected by the manager
     */
    REJECTED("Rejected"),

    /**
     * The leave request has been cancelled by the employee
     */
    CANCELLED("Cancelled"),

    /**
     * The leave request requires more information before a decision can be made
     */
    INFORMATION_REQUIRED("Information Required"),

    /**
     * The leave request has been approved but is pending higher-level approval
     */
    PARTIALLY_APPROVED("Partially Approved"),

    /**
     * The leave has been taken (used for tracking actual vs. planned leave)
     */
    TAKEN("Taken"),

    /**
     * The leave was approved but the employee did not take it
     */
    NOT_TAKEN("Not Taken");

    private final String displayName;

    /**
     * Constructor for LeaveStatus
     * @param displayName The human-readable name for display purposes
     */
    LeaveStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the status
     * @return The human-readable status name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if the status is a final status (no further action needed)
     * @return true if the status is final, false otherwise
     */
    public boolean isFinalStatus() {
        return this == APPROVED || this == REJECTED || this == CANCELLED ||
                this == TAKEN || this == NOT_TAKEN;
    }

    /**
     * Checks if the status is an active status (leave is or will be taken)
     * @return true if the status is active, false otherwise
     */
    public boolean isActiveStatus() {
        return this == APPROVED || this == PARTIALLY_APPROVED || this == TAKEN;
    }

    /**
     * Checks if the status requires action from the manager
     * @return true if manager action is required, false otherwise
     */
    public boolean requiresManagerAction() {
        return this == PENDING;
    }

    /**
     * Checks if the status requires action from the employee
     * @return true if employee action is required, false otherwise
     */
    public boolean requiresEmployeeAction() {
        return this == INFORMATION_REQUIRED;
    }

    /**
     * Returns the display name when the enum is converted to a string
     * @return The display name
     */
    @Override
    public String toString() {
        return displayName;
    }
}
