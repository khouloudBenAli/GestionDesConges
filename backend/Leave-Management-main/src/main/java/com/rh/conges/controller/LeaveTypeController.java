package com.rh.conges.controller;

import com.rh.conges.model.LeaveType;
import com.rh.conges.service.LeaveTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-types")
public class LeaveTypeController {

    @Autowired
    private LeaveTypeService leaveTypeService;

    /**
     * Retrieves all leave types
     * @return List of leave types
     */
    @GetMapping
    public ResponseEntity<List<LeaveType>> getAllLeaveTypes() {
        List<LeaveType> leaveTypes = leaveTypeService.getAllLeaveTypes();
        return new ResponseEntity<>(leaveTypes, HttpStatus.OK);
    }

    /**
     * Retrieves a leave type by ID
     * @param id Leave type ID
     * @return The found leave type
     */
    @GetMapping("/{id}")
    public ResponseEntity<LeaveType> getLeaveTypeById(@PathVariable Long id) {
        try {
            LeaveType leaveType = leaveTypeService.getLeaveTypeById(id);
            return new ResponseEntity<>(leaveType, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Creates a new leave type
     * @param leaveType Leave type data to create
     * @return The created leave type
     */
    @PostMapping
    public ResponseEntity<LeaveType> createLeaveType(@RequestBody LeaveType leaveType) {
        LeaveType savedLeaveType = leaveTypeService.saveLeaveType(leaveType);
        return new ResponseEntity<>(savedLeaveType, HttpStatus.CREATED);
    }

    /**
     * Updates an existing leave type
     * @param id ID of the leave type to update
     * @param leaveType New leave type data
     * @return The updated leave type
     */
    @PutMapping("/{id}")
    public ResponseEntity<LeaveType> updateLeaveType(@PathVariable Long id, @RequestBody LeaveType leaveType) {
        try {
            // Check if leave type exists
            leaveTypeService.getLeaveTypeById(id);

            // Update the leave type
            leaveType.setId(id);
            LeaveType updatedLeaveType = leaveTypeService.saveLeaveType(leaveType);

            return new ResponseEntity<>(updatedLeaveType, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a leave type
     * @param id ID of the leave type to delete
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable Long id) {
        try {
            // Check if leave type exists
            leaveTypeService.getLeaveTypeById(id);

            // Delete the leave type
            leaveTypeService.deleteLeaveType(id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves all active leave types
     * @return List of active leave types
     */
    @GetMapping("/active")
    public ResponseEntity<List<LeaveType>> getActiveLeaveTypes() {
        List<LeaveType> leaveTypes = leaveTypeService.getAllLeaveTypes().stream()
                .filter(lt -> Boolean.TRUE.equals(lt.getActive()))
                .toList();
        return new ResponseEntity<>(leaveTypes, HttpStatus.OK);
    }

    /**
     * Retrieves all paid leave types
     * @return List of paid leave types
     */
    @GetMapping("/paid")
    public ResponseEntity<List<LeaveType>> getPaidLeaveTypes() {
        List<LeaveType> leaveTypes = leaveTypeService.getAllLeaveTypes().stream()
                .filter(LeaveType::isPaidLeave)
                .toList();
        return new ResponseEntity<>(leaveTypes, HttpStatus.OK);
    }

    /**
     * Retrieves all unpaid leave types
     * @return List of unpaid leave types
     */
    @GetMapping("/unpaid")
    public ResponseEntity<List<LeaveType>> getUnpaidLeaveTypes() {
        List<LeaveType> leaveTypes = leaveTypeService.getAllLeaveTypes().stream()
                .filter(leaveType -> !leaveType.isPaidLeave())
                .toList();
        return new ResponseEntity<>(leaveTypes, HttpStatus.OK);
    }
}
