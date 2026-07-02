package com.rh.conges.service;

import com.rh.conges.model.Employee;
import com.rh.conges.model.LeaveBalance;
import com.rh.conges.model.LeaveRequest;
import com.rh.conges.repository.EmployeeRepository;
import com.rh.conges.repository.LeaveBalanceRepository;
import com.rh.conges.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for exporting data to CSV format
 */
@Service
public class CsvExportService {

    private static final String CSV_SEPARATOR = ",";
    private static final String CSV_NEW_LINE = "\n";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    /**
     * Exports all employees to CSV
     * @return CSV content as byte array
     * @throws IOException if export fails
     */
    public byte[] exportEmployeesToCsv() throws IOException {
        List<Employee> employees = employeeRepository.findAll();
        return exportEmployeesToCsv(employees);
    }

    /**
     * Exports a list of employees to CSV
     * @param employees List of employees to export
     * @return CSV content as byte array
     * @throws IOException if export fails
     */
    public byte[] exportEmployeesToCsv(List<Employee> employees) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        // Write BOM for Excel UTF-8 compatibility
        writer.write('\uFEFF');

        // Write header
        writer.write("ID,First Name,Last Name,Email,Phone Number,Job Title,Department,Manager,Hire Date");
        writer.write(CSV_NEW_LINE);

        // Write data
        for (Employee employee : employees) {
            writer.write(escapeSpecialCharacters(employee.getId()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(employee.getFirstName()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(employee.getLastName()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(employee.getEmail()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(employee.getPhoneNumber()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(employee.getJobTitle()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(employee.getDepartment() != null ? employee.getDepartment().getName() : ""));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(employee.getManager() != null ? employee.getManager().getFullName() : ""));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(employee.getHireDate() != null ? employee.getHireDate().format(DATE_FORMATTER) : ""));
            writer.write(CSV_NEW_LINE);
        }

        writer.flush();
        return outputStream.toByteArray();
    }

    /**
     * Exports all leave requests to CSV
     * @return CSV content as byte array
     * @throws IOException if export fails
     */
    public byte[] exportLeaveRequestsToCsv() throws IOException {
        List<LeaveRequest> requests = leaveRequestRepository.findAll();
        return exportLeaveRequestsToCsv(requests);
    }

    /**
     * Exports a list of leave requests to CSV
     * @param requests List of leave requests to export
     * @return CSV content as byte array
     * @throws IOException if export fails
     */
    public byte[] exportLeaveRequestsToCsv(List<LeaveRequest> requests) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        // Write BOM for Excel UTF-8 compatibility
        writer.write('\uFEFF');

        // Write header
        writer.write("ID,Employee,Leave Type,Start Date,End Date,Duration (days),Status,Reason,Manager Comment,Request Date,Response Date");
        writer.write(CSV_NEW_LINE);

        // Write data
        for (LeaveRequest request : requests) {
            writer.write(escapeSpecialCharacters(request.getId()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(request.getEmployee().getFullName()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(request.getLeaveType().getName()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(request.getStartDate() != null ? request.getStartDate().format(DATE_FORMATTER) : ""));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(request.getEndDate() != null ? request.getEndDate().format(DATE_FORMATTER) : ""));
            writer.write(CSV_SEPARATOR);
            writer.write(String.valueOf(request.getDurationInDays()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(request.getStatus().name()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(request.getReason()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(request.getManagerComment()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(request.getRequestDate() != null ? request.getRequestDate().format(DATETIME_FORMATTER) : ""));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(request.getResponseDate() != null ? request.getResponseDate().format(DATETIME_FORMATTER) : ""));
            writer.write(CSV_NEW_LINE);
        }

        writer.flush();
        return outputStream.toByteArray();
    }

    /**
     * Exports all leave balances to CSV
     * @return CSV content as byte array
     * @throws IOException if export fails
     */
    public byte[] exportLeaveBalancesToCsv() throws IOException {
        List<LeaveBalance> balances = leaveBalanceRepository.findAll();
        return exportLeaveBalancesToCsv(balances);
    }

    /**
     * Exports a list of leave balances to CSV
     * @param balances List of leave balances to export
     * @return CSV content as byte array
     * @throws IOException if export fails
     */
    public byte[] exportLeaveBalancesToCsv(List<LeaveBalance> balances) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        // Write BOM for Excel UTF-8 compatibility
        writer.write('\uFEFF');

        // Write header
        writer.write("ID,Employee,Leave Type,Year,Total Days,Used Days,Carried Over Days,Additional Days,Remaining Days,Last Updated");
        writer.write(CSV_NEW_LINE);

        // Write data
        for (LeaveBalance balance : balances) {
            writer.write(escapeSpecialCharacters(balance.getId()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(balance.getEmployee().getFullName()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(balance.getLeaveType().getName()));
            writer.write(CSV_SEPARATOR);
            writer.write(String.valueOf(balance.getYear()));
            writer.write(CSV_SEPARATOR);
            writer.write(String.valueOf(balance.getTotalDays()));
            writer.write(CSV_SEPARATOR);
            writer.write(String.valueOf(balance.getUsedDays()));
            writer.write(CSV_SEPARATOR);
            writer.write(String.valueOf(balance.getCarriedOverDays() != null ? balance.getCarriedOverDays() : 0));
            writer.write(CSV_SEPARATOR);
            writer.write(String.valueOf(balance.getAdditionalDays() != null ? balance.getAdditionalDays() : 0));
            writer.write(CSV_SEPARATOR);
            writer.write(String.valueOf(balance.getRemainingDays()));
            writer.write(CSV_SEPARATOR);
            writer.write(escapeSpecialCharacters(balance.getLastUpdated() != null ? balance.getLastUpdated().format(DATETIME_FORMATTER) : ""));
            writer.write(CSV_NEW_LINE);
        }

        writer.flush();
        return outputStream.toByteArray();
    }

    /**
     * Escapes special characters in CSV fields
     * @param value The value to escape
     * @return Escaped value
     */
    private String escapeSpecialCharacters(Object value) {
        if (value == null) {
            return "";
        }

        String data = value.toString();

        // If data contains comma, newline, or quote, wrap it in quotes
        if (data.contains(",") || data.contains("\n") || data.contains("\"")) {
            // Escape existing quotes by doubling them
            data = data.replace("\"", "\"\"");
            // Wrap in quotes
            data = "\"" + data + "\"";
        }

        return data;
    }
}

