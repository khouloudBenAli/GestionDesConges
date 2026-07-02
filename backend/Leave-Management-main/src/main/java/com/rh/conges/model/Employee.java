package com.rh.conges.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an employee in the organization
 */
@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "job_title")
    private String jobTitle;

    /**
     * The department this employee belongs to
     */
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * The manager of this employee
     */
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;

    /**
     * Employees managed by this employee
     */
    @OneToMany(mappedBy = "manager")
    @JsonIgnore
    private List<Employee> subordinates = new ArrayList<>();

    /**
     * Leave requests submitted by this employee
     */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<LeaveRequest> leaveRequests = new ArrayList<>();

    /**
     * Leave balances for this employee
     */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<LeaveBalance> leaveBalances = new ArrayList<>();

    /**
     * Returns the full name of the employee
     * @return First name and last name combined
     */
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Adds a subordinate to this employee (as manager)
     * @param subordinate The employee to add as subordinate
     */
    public void addSubordinate(Employee subordinate) {
        subordinates.add(subordinate);
        subordinate.setManager(this);
    }

    /**
     * Removes a subordinate from this employee
     * @param subordinate The employee to remove as subordinate
     */
    public void removeSubordinate(Employee subordinate) {
        subordinates.remove(subordinate);
        subordinate.setManager(null);
    }

    /**
     * Adds a leave request for this employee
     * @param leaveRequest The leave request to add
     */
    public void addLeaveRequest(LeaveRequest leaveRequest) {
        leaveRequests.add(leaveRequest);
        leaveRequest.setEmployee(this);
    }

    /**
     * Adds a leave balance for this employee
     * @param leaveBalance The leave balance to add
     */
    public void addLeaveBalance(LeaveBalance leaveBalance) {
        leaveBalances.add(leaveBalance);
        leaveBalance.setEmployee(this);
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public Employee getManager() {
        return manager;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Department getDepartment() {
        return department;
    }

    public List<Employee> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(List<Employee> subordinates) {
        this.subordinates = subordinates;
    }

    public List<LeaveRequest> getLeaveRequests() {
        return leaveRequests;
    }

    public void setLeaveRequests(List<LeaveRequest> leaveRequests) {
        this.leaveRequests = leaveRequests;
    }

    public List<LeaveBalance> getLeaveBalances() {
        return leaveBalances;
    }

    public void setLeaveBalances(List<LeaveBalance> leaveBalances) {
        this.leaveBalances = leaveBalances;
    }
}
