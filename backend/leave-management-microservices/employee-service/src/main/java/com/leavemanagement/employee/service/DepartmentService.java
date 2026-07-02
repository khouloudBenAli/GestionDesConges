package com.leavemanagement.employee.service;

import com.leavemanagement.employee.dto.DepartmentDTO;
import com.leavemanagement.employee.dto.DepartmentRequest;
import com.leavemanagement.employee.entity.Department;
import com.leavemanagement.employee.exception.BusinessException;
import com.leavemanagement.employee.exception.ResourceNotFoundException;
import com.leavemanagement.employee.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findByIdWithEmployees(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département", id));
        return toDTO(department);
    }

    @Transactional
    public DepartmentDTO createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new BusinessException("Un département avec le nom '" + request.getName() + "' existe déjà");
        }
        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return toDTO(departmentRepository.save(department));
    }

    @Transactional
    public DepartmentDTO updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département", id));

        if (departmentRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BusinessException("Un département avec le nom '" + request.getName() + "' existe déjà");
        }
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        return toDTO(departmentRepository.save(department));
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findByIdWithEmployees(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département", id));

        if (!department.getEmployees().isEmpty()) {
            throw new BusinessException("Impossible de supprimer un département contenant des employés. Transférez d'abord les employés.");
        }
        departmentRepository.delete(department);
    }

    public List<DepartmentDTO> getEmployeesByDepartment(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Département", departmentId);
        }
        return departmentRepository.findAll().stream()
                .filter(d -> d.getId().equals(departmentId))
                .map(this::toDTO)
                .toList();
    }

    private DepartmentDTO toDTO(Department department) {
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .employeeCount(department.getEmployees() != null ? department.getEmployees().size() : 0)
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
}
