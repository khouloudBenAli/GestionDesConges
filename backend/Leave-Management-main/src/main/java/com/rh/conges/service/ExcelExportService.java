package com.rh.conges.service;

import com.rh.conges.model.Employee;
import com.rh.conges.model.LeaveBalance;
import com.rh.conges.model.LeaveRequest;
import com.rh.conges.repository.EmployeeRepository;
import com.rh.conges.repository.LeaveBalanceRepository;
import com.rh.conges.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Service for exporting data to Excel format
 *
 * NOTE: This service is currently disabled because it requires Apache POI dependency.
 *
 * To enable Excel export functionality:
 * 1. Add the following dependencies to pom.xml:
 *    <dependency>
 *        <groupId>org.apache.poi</groupId>
 *        <artifactId>poi</artifactId>
 *        <version>5.2.5</version>
 *    </dependency>
 *    <dependency>
 *        <groupId>org.apache.poi</groupId>
 *        <artifactId>poi-ooxml</artifactId>
 *        <version>5.2.5</version>
 *    </dependency>
 *
 * 2. Uncomment the code below
 * 3. Recompile: mvn clean install
 * 4. Use the export endpoints: /api/export/employees/excel
 */
@Service
public class ExcelExportService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    /**
     * Placeholder method - uncomment after adding Apache POI
     */
    public byte[] exportEmployeesToExcel() throws IOException {
        throw new UnsupportedOperationException(
            "Excel export requires Apache POI dependency. Please add poi and poi-ooxml to pom.xml"
        );
    }

    /**
     * Placeholder method - uncomment after adding Apache POI
     */
    public byte[] exportEmployeesToExcel(List<Employee> employees) throws IOException {
        throw new UnsupportedOperationException(
            "Excel export requires Apache POI dependency. Please add poi and poi-ooxml to pom.xml"
        );
    }

    /**
     * Placeholder method - uncomment after adding Apache POI
     */
    public byte[] exportLeaveRequestsToExcel() throws IOException {
        throw new UnsupportedOperationException(
            "Excel export requires Apache POI dependency. Please add poi and poi-ooxml to pom.xml"
        );
    }

    /**
     * Placeholder method - uncomment after adding Apache POI
     */
    public byte[] exportLeaveRequestsToExcel(List<LeaveRequest> requests) throws IOException {
        throw new UnsupportedOperationException(
            "Excel export requires Apache POI dependency. Please add poi and poi-ooxml to pom.xml"
        );
    }

    /**
     * Placeholder method - uncomment after adding Apache POI
     */
    public byte[] exportLeaveBalancesToExcel() throws IOException {
        throw new UnsupportedOperationException(
            "Excel export requires Apache POI dependency. Please add poi and poi-ooxml to pom.xml"
        );
    }

    /**
     * Placeholder method - uncomment after adding Apache POI
     */
    public byte[] exportLeaveBalancesToExcel(List<LeaveBalance> balances) throws IOException {
        throw new UnsupportedOperationException(
            "Excel export requires Apache POI dependency. Please add poi and poi-ooxml to pom.xml"
        );
    }

    // ========== UNCOMMENT CODE BELOW AFTER ADDING APACHE POI ==========

    /*
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] exportEmployeesToExcel() throws IOException {
        List<Employee> employees = employeeRepository.findAll();
        return exportEmployeesToExcel(employees);
    }

    public byte[] exportEmployeesToExcel(List<Employee> employees) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "First Name", "Last Name", "Email", "Phone Number",
                           "Job Title", "Department", "Manager", "Hire Date"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Employee employee : employees) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, employee.getId(), dataStyle);
            createCell(row, 1, employee.getFirstName(), dataStyle);
            createCell(row, 2, employee.getLastName(), dataStyle);
            createCell(row, 3, employee.getEmail(), dataStyle);
            createCell(row, 4, employee.getPhoneNumber(), dataStyle);
            createCell(row, 5, employee.getJobTitle(), dataStyle);
            createCell(row, 6, employee.getDepartment() != null ? employee.getDepartment().getName() : "", dataStyle);
            createCell(row, 7, employee.getManager() != null ? employee.getManager().getFullName() : "", dataStyle);
            createCell(row, 8, employee.getHireDate() != null ? employee.getHireDate().format(DATE_FORMATTER) : "", dataStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    public byte[] exportLeaveRequestsToExcel() throws IOException {
        List<LeaveRequest> requests = leaveRequestRepository.findAll();
        return exportLeaveRequestsToExcel(requests);
    }

    public byte[] exportLeaveRequestsToExcel(List<LeaveRequest> requests) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Leave Requests");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Employee", "Leave Type", "Start Date", "End Date",
                           "Duration (days)", "Status", "Reason", "Manager Comment",
                           "Request Date", "Response Date"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (LeaveRequest request : requests) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, request.getId(), dataStyle);
            createCell(row, 1, request.getEmployee().getFullName(), dataStyle);
            createCell(row, 2, request.getLeaveType().getName(), dataStyle);
            createCell(row, 3, request.getStartDate() != null ? request.getStartDate().format(DATE_FORMATTER) : "", dataStyle);
            createCell(row, 4, request.getEndDate() != null ? request.getEndDate().format(DATE_FORMATTER) : "", dataStyle);
            createCell(row, 5, request.getDurationInDays(), dataStyle);
            createCell(row, 6, request.getStatus().name(), dataStyle);
            createCell(row, 7, request.getReason(), dataStyle);
            createCell(row, 8, request.getManagerComment(), dataStyle);
            createCell(row, 9, request.getRequestDate() != null ? request.getRequestDate().format(DATETIME_FORMATTER) : "", dataStyle);
            createCell(row, 10, request.getResponseDate() != null ? request.getResponseDate().format(DATETIME_FORMATTER) : "", dataStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    public byte[] exportLeaveBalancesToExcel() throws IOException {
        List<LeaveBalance> balances = leaveBalanceRepository.findAll();
        return exportLeaveBalancesToExcel(balances);
    }

    public byte[] exportLeaveBalancesToExcel(List<LeaveBalance> balances) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Leave Balances");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Employee", "Leave Type", "Year", "Total Days",
                           "Used Days", "Carried Over Days", "Additional Days",
                           "Remaining Days", "Last Updated"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (LeaveBalance balance : balances) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, balance.getId(), dataStyle);
            createCell(row, 1, balance.getEmployee().getFullName(), dataStyle);
            createCell(row, 2, balance.getLeaveType().getName(), dataStyle);
            createCell(row, 3, balance.getYear(), dataStyle);
            createCell(row, 4, balance.getTotalDays(), dataStyle);
            createCell(row, 5, balance.getUsedDays(), dataStyle);
            createCell(row, 6, balance.getCarriedOverDays() != null ? balance.getCarriedOverDays() : 0, dataStyle);
            createCell(row, 7, balance.getAdditionalDays() != null ? balance.getAdditionalDays() : 0, dataStyle);
            createCell(row, 8, balance.getRemainingDays(), dataStyle);
            createCell(row, 9, balance.getLastUpdated() != null ? balance.getLastUpdated().format(DATETIME_FORMATTER) : "", dataStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);

        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }

        cell.setCellStyle(style);
    }
    */
}

