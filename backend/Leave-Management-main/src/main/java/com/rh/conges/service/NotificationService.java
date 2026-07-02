package com.rh.conges.service;

import com.rh.conges.model.Employee;
import com.rh.conges.model.LeaveRequest;
import com.rh.conges.model.LeaveStatus;
import com.rh.conges.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for sending notifications related to leave requests
 * Note: This is a simplified version. For full email functionality, add dependencies:
 * - spring-boot-starter-mail
 * - spring-boot-starter-thymeleaf
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Value("${spring.mail.username:noreply@example.com}")
    private String fromEmail;

    @Value("${application.name:Leave Management System}")
    private String applicationName;

    @Value("${application.url:http://localhost:8080}")
    private String applicationUrl;

    /**
     * Sends a notification for a new leave request
     * @param leaveRequest The leave request
     */
    @Async
    public void sendNewLeaveRequestNotification(LeaveRequest leaveRequest) {
        Employee employee = leaveRequest.getEmployee();
        Employee manager = employee.getManager();

        if (manager == null || manager.getEmail() == null) {
            logger.warn("Cannot send notification: employee {} has no manager or manager has no email",
                    employee.getId());
            return;
        }

        String message = String.format(
                "New Leave Request from %s %s\n" +
                "Leave Type: %s\n" +
                "Period: %s to %s\n" +
                "Duration: %d days\n" +
                "Reason: %s",
                employee.getFirstName(), employee.getLastName(),
                leaveRequest.getLeaveType().getName(),
                formatDate(leaveRequest.getStartDate()),
                formatDate(leaveRequest.getEndDate()),
                leaveRequest.getDurationInDays(),
                leaveRequest.getReason()
        );

        logger.info("EMAIL NOTIFICATION: To: {} | Subject: New Leave Request | Message: {}",
                manager.getEmail(), message);

        // TODO: Implement actual email sending when spring-boot-starter-mail is added
    }

    /**
     * Sends a notification for a leave request status update
     * @param leaveRequest The leave request
     * @param previousStatus The previous status
     */
    @Async
    public void sendLeaveRequestStatusUpdateNotification(LeaveRequest leaveRequest, LeaveStatus previousStatus) {
        Employee employee = leaveRequest.getEmployee();

        if (employee.getEmail() == null) {
            logger.warn("Cannot send notification: employee {} has no email", employee.getId());
            return;
        }

        String statusMessage = "Leave Request Status Updated";
        if (leaveRequest.getStatus() == LeaveStatus.APPROVED) {
            statusMessage = "Leave Request Approved";
        } else if (leaveRequest.getStatus() == LeaveStatus.REJECTED) {
            statusMessage = "Leave Request Rejected";
        } else if (leaveRequest.getStatus() == LeaveStatus.INFORMATION_REQUIRED) {
            statusMessage = "Additional Information Required for Leave Request";
        }

        String message = String.format(
                "Leave Request Status Update\n" +
                "Leave Type: %s\n" +
                "Period: %s to %s\n" +
                "Previous Status: %s\n" +
                "New Status: %s\n" +
                "Manager Comment: %s",
                leaveRequest.getLeaveType().getName(),
                formatDate(leaveRequest.getStartDate()),
                formatDate(leaveRequest.getEndDate()),
                previousStatus.getDisplayName(),
                leaveRequest.getStatus().getDisplayName(),
                leaveRequest.getManagerComment()
        );

        logger.info("EMAIL NOTIFICATION: To: {} | Subject: {} | Message: {}",
                employee.getEmail(), statusMessage, message);

        // TODO: Implement actual email sending when spring-boot-starter-mail is added
    }

    /**
     * Sends a reminder for pending leave requests
     * @param manager The manager
     * @param pendingRequests List of pending leave requests
     */
    @Async
    public void sendPendingLeaveRequestsReminder(Employee manager, List<LeaveRequest> pendingRequests) {
        if (manager.getEmail() == null) {
            logger.warn("Cannot send reminder: manager {} has no email", manager.getId());
            return;
        }

        if (pendingRequests.isEmpty()) {
            logger.debug("No pending requests for manager {}, skipping reminder", manager.getId());
            return;
        }

        String message = String.format(
                "Reminder: You have %d pending leave requests requiring your attention.",
                pendingRequests.size()
        );

        logger.info("EMAIL NOTIFICATION: To: {} | Subject: Pending Leave Requests | Message: {}",
                manager.getEmail(), message);

        // TODO: Implement actual email sending when spring-boot-starter-mail is added
    }

    /**
     * Sends a notification for upcoming leave
     * @param leaveRequest The leave request
     * @param daysUntilLeave Number of days until the leave starts
     */
    @Async
    public void sendUpcomingLeaveNotification(LeaveRequest leaveRequest, int daysUntilLeave) {
        Employee employee = leaveRequest.getEmployee();
        Employee manager = employee.getManager();

        if (employee.getEmail() == null) {
            logger.warn("Cannot send notification: employee {} has no email", employee.getId());
            return;
        }

        String message = String.format(
                "Reminder: Your leave starts in %d days\n" +
                "Leave Type: %s\n" +
                "Period: %s to %s\n" +
                "Duration: %d days",
                daysUntilLeave,
                leaveRequest.getLeaveType().getName(),
                formatDate(leaveRequest.getStartDate()),
                formatDate(leaveRequest.getEndDate()),
                leaveRequest.getDurationInDays()
        );

        logger.info("EMAIL NOTIFICATION: To: {} | Subject: Upcoming Leave Reminder | Message: {}",
                employee.getEmail(), message);

        // Also notify the manager if available
        if (manager != null && manager.getEmail() != null) {
            String managerMessage = String.format(
                    "Reminder: %s %s's leave starts in %d days\n" +
                    "Leave Type: %s\n" +
                    "Period: %s to %s",
                    employee.getFirstName(), employee.getLastName(),
                    daysUntilLeave,
                    leaveRequest.getLeaveType().getName(),
                    formatDate(leaveRequest.getStartDate()),
                    formatDate(leaveRequest.getEndDate())
            );

            logger.info("EMAIL NOTIFICATION: To: {} | Subject: Team Member Upcoming Leave | Message: {}",
                    manager.getEmail(), managerMessage);
        }

        // TODO: Implement actual email sending when spring-boot-starter-mail is added
    }

    /**
     * Sends a notification for low leave balance
     * @param employee The employee
     * @param leaveTypeName The leave type name
     * @param remainingDays Remaining days
     */
    @Async
    public void sendLowLeaveBalanceNotification(Employee employee, String leaveTypeName, int remainingDays) {
        if (employee.getEmail() == null) {
            logger.warn("Cannot send notification: employee {} has no email", employee.getId());
            return;
        }

        String message = String.format(
                "Low Leave Balance Alert\n" +
                "Leave Type: %s\n" +
                "Remaining Days: %d",
                leaveTypeName,
                remainingDays
        );

        logger.info("EMAIL NOTIFICATION: To: {} | Subject: Low Leave Balance | Message: {}",
                employee.getEmail(), message);

        // TODO: Implement actual email sending when spring-boot-starter-mail is added
    }

    /**
     * Sends a notification for leave balance updates
     * @param employee The employee
     * @param leaveTypeName The leave type name
     * @param previousBalance Previous balance
     * @param newBalance New balance
     * @param reason Reason for the update
     */
    @Async
    public void sendLeaveBalanceUpdateNotification(Employee employee, String leaveTypeName,
                                                   int previousBalance, int newBalance, String reason) {
        if (employee.getEmail() == null) {
            logger.warn("Cannot send notification: employee {} has no email", employee.getId());
            return;
        }

        String message = String.format(
                "Leave Balance Updated\n" +
                "Leave Type: %s\n" +
                "Previous Balance: %d days\n" +
                "New Balance: %d days\n" +
                "Difference: %d days\n" +
                "Reason: %s",
                leaveTypeName,
                previousBalance,
                newBalance,
                newBalance - previousBalance,
                reason
        );

        logger.info("EMAIL NOTIFICATION: To: {} | Subject: Leave Balance Updated | Message: {}",
                employee.getEmail(), message);

        // TODO: Implement actual email sending when spring-boot-starter-mail is added
    }

    /**
     * Sends a simple text email
     * @param to Recipient email address
     * @param subject Email subject
     * @param text Email body
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        logger.info("EMAIL NOTIFICATION: To: {} | Subject: {} | Message: {}",
                to, subject, text);

        // TODO: Implement actual email sending when spring-boot-starter-mail is added
    }

    /**
     * Formats a date for display
     * @param date The date to format
     * @return Formatted date string
     */
    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
    }
}
