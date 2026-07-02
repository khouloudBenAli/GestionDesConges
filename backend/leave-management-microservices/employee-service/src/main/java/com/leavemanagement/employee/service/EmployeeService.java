package com.leavemanagement.employee.service;

import com.leavemanagement.employee.dto.EmployeeDTO;
import com.leavemanagement.employee.dto.EmployeeRequest;
import com.leavemanagement.employee.entity.Department;
import com.leavemanagement.employee.entity.Employee;
import com.leavemanagement.employee.event.EmployeeCreatedEvent;
import com.leavemanagement.employee.exception.BusinessException;
import com.leavemanagement.employee.exception.ResourceNotFoundException;
import com.leavemanagement.employee.kafka.EmployeeEventProducer;
import com.leavemanagement.employee.repository.DepartmentRepository;
import com.leavemanagement.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeEventProducer eventProducer;

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public EmployeeDTO getEmployeeById(Long id) {
        return employeeRepository.findByIdWithDepartment(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", id));
    }

    public EmployeeDTO getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'email: " + email));
    }

    public List<EmployeeDTO> getEmployeesByDepartment(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Département", departmentId);
        }
        return employeeRepository.findByDepartmentId(departmentId).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<EmployeeDTO> searchEmployees(String keyword) {
        return employeeRepository.searchByName(keyword).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public EmployeeDTO createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Un employé avec l'email '" + request.getEmail() + "' existe déjà");
        }

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .jobTitle(request.getJobTitle())
                .hireDate(request.getHireDate())
                .build();

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Département", request.getDepartmentId()));
            employee.setDepartment(department);
        }

        Employee saved = employeeRepository.save(employee);

        // Publier l'événement Kafka pour initialiser les soldes de congés
        eventProducer.publishEmployeeCreated(EmployeeCreatedEvent.builder()
                .employeeId(saved.getId())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .email(saved.getEmail())
                .hireDate(saved.getHireDate())
                .build());

        return toDTO(saved);
    }

    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", id));

        if (employeeRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new BusinessException("Un employé avec l'email '" + request.getEmail() + "' existe déjà");
        }

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setJobTitle(request.getJobTitle());
        employee.setHireDate(request.getHireDate());

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Département", request.getDepartmentId()));
            employee.setDepartment(department);
        } else {
            employee.setDepartment(null);
        }

        return toDTO(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeDTO assignDepartment(Long employeeId, Long departmentId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employé", employeeId));
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Département", departmentId));

        employee.setDepartment(department);
        return toDTO(employeeRepository.save(employee));
    }

    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employé", id);
        }
        employeeRepository.deleteById(id);
    }

    private EmployeeDTO toDTO(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .jobTitle(employee.getJobTitle())
                .hireDate(employee.getHireDate())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
