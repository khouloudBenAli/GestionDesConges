package com.rh.conges.controller;

import com.rh.conges.service.CsvExportService;
import com.rh.conges.service.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * REST Controller for exporting data to various formats (CSV, Excel)
 */
@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "*")
public class ExportController {

    @Autowired
    private CsvExportService csvExportService;

    @Autowired
    private ExcelExportService excelExportService;

    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    // ========== CSV EXPORTS ==========

    /**
     * Exports all employees to CSV
     * @return CSV file
     */
    @GetMapping("/employees/csv")
    public ResponseEntity<byte[]> exportEmployeesCsv() {
        try {
            byte[] data = csvExportService.exportEmployeesToCsv();
            String filename = "employees_" + LocalDateTime.now().format(FILENAME_FORMATTER) + ".csv";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exports all leave requests to CSV
     * @return CSV file
     */
    @GetMapping("/leave-requests/csv")
    public ResponseEntity<byte[]> exportLeaveRequestsCsv() {
        try {
            byte[] data = csvExportService.exportLeaveRequestsToCsv();
            String filename = "leave_requests_" + LocalDateTime.now().format(FILENAME_FORMATTER) + ".csv";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exports all leave balances to CSV
     * @return CSV file
     */
    @GetMapping("/leave-balances/csv")
    public ResponseEntity<byte[]> exportLeaveBalancesCsv() {
        try {
            byte[] data = csvExportService.exportLeaveBalancesToCsv();
            String filename = "leave_balances_" + LocalDateTime.now().format(FILENAME_FORMATTER) + ".csv";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== EXCEL EXPORTS ==========

    /**
     * Exports all employees to Excel
     * @return Excel file
     */
    @GetMapping("/employees/excel")
    public ResponseEntity<byte[]> exportEmployeesExcel() {
        try {
            byte[] data = excelExportService.exportEmployeesToExcel();
            String filename = "employees_" + LocalDateTime.now().format(FILENAME_FORMATTER) + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exports all leave requests to Excel
     * @return Excel file
     */
    @GetMapping("/leave-requests/excel")
    public ResponseEntity<byte[]> exportLeaveRequestsExcel() {
        try {
            byte[] data = excelExportService.exportLeaveRequestsToExcel();
            String filename = "leave_requests_" + LocalDateTime.now().format(FILENAME_FORMATTER) + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exports all leave balances to Excel
     * @return Excel file
     */
    @GetMapping("/leave-balances/excel")
    public ResponseEntity<byte[]> exportLeaveBalancesExcel() {
        try {
            byte[] data = excelExportService.exportLeaveBalancesToExcel();
            String filename = "leave_balances_" + LocalDateTime.now().format(FILENAME_FORMATTER) + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== COMBINED REPORT ==========

    /**
     * Exports a complete report with all data to Excel (multiple sheets)
     * @return Excel file with multiple sheets
     */
    @GetMapping("/full-report/excel")
    public ResponseEntity<byte[]> exportFullReportExcel() {
        try {
            // TODO: Implement combined report with multiple sheets
            // For now, just export employees
            byte[] data = excelExportService.exportEmployeesToExcel();
            String filename = "full_report_" + LocalDateTime.now().format(FILENAME_FORMATTER) + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

