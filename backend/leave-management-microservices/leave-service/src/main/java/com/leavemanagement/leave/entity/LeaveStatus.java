package com.leavemanagement.leave.entity;

public enum LeaveStatus {
    PENDING("En attente"),
    APPROVED("Approuvé"),
    REJECTED("Rejeté"),
    CANCELLED("Annulé");

    private final String displayName;

    LeaveStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isFinalStatus() {
        return this == REJECTED || this == CANCELLED;
    }

    public boolean canBeApproved() {
        return this == PENDING;
    }

    public boolean canBeRejected() {
        return this == PENDING;
    }

    public boolean canBeCancelled() {
        return this == PENDING || this == APPROVED;
    }
}
