package com.leavemanagement.employee.service;

import com.leavemanagement.employee.dto.DepartmentDTO;
import com.leavemanagement.employee.dto.DepartmentRequest;
import com.leavemanagement.employee.entity.Department;
import com.leavemanagement.employee.exception.BusinessException;
import com.leavemanagement.employee.exception.ResourceNotFoundException;
import com.leavemanagement.employee.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - DepartmentService")
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department department;
    private DepartmentRequest request;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .name("Informatique")
                .description("Département IT")
                .build();

        request = new DepartmentRequest("Informatique", "Département IT");
    }

    @Test
    @DisplayName("getAllDepartments - retourne la liste des départements")
    void getAllDepartments_shouldReturnList() {
        when(departmentRepository.findAll()).thenReturn(List.of(department));

        List<DepartmentDTO> result = departmentService.getAllDepartments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Informatique");
    }

    @Test
    @DisplayName("getDepartmentById - retourne le département quand il existe")
    void getDepartmentById_whenExists_shouldReturnDTO() {
        when(departmentRepository.findByIdWithEmployees(1L)).thenReturn(Optional.of(department));

        DepartmentDTO result = departmentService.getDepartmentById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Informatique");
    }

    @Test
    @DisplayName("getDepartmentById - lève une exception quand il n'existe pas")
    void getDepartmentById_whenNotExists_shouldThrow() {
        when(departmentRepository.findByIdWithEmployees(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("createDepartment - crée avec succès")
    void createDepartment_shouldCreateSuccessfully() {
        when(departmentRepository.existsByName("Informatique")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        DepartmentDTO result = departmentService.createDepartment(request);

        assertThat(result.getName()).isEqualTo("Informatique");
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    @DisplayName("createDepartment - lève une exception si le nom existe déjà")
    void createDepartment_whenNameExists_shouldThrow() {
        when(departmentRepository.existsByName("Informatique")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.createDepartment(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("existe déjà");

        verify(departmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteDepartment - supprime un département vide")
    void deleteDepartment_whenEmpty_shouldDelete() {
        department.setEmployees(List.of());
        when(departmentRepository.findByIdWithEmployees(1L)).thenReturn(Optional.of(department));

        departmentService.deleteDepartment(1L);

        verify(departmentRepository).delete(department);
    }

    @Test
    @DisplayName("deleteDepartment - lève exception si département non vide")
    void deleteDepartment_whenNotEmpty_shouldThrow() {
        department.getEmployees().add(new com.leavemanagement.employee.entity.Employee());
        when(departmentRepository.findByIdWithEmployees(1L)).thenReturn(Optional.of(department));

        assertThatThrownBy(() -> departmentService.deleteDepartment(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("employés");

        verify(departmentRepository, never()).delete(any());
    }
}
