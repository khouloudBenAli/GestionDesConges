package com.leavemanagement.employee.service;

import com.leavemanagement.employee.dto.EmployeeDTO;
import com.leavemanagement.employee.dto.EmployeeRequest;
import com.leavemanagement.employee.entity.Department;
import com.leavemanagement.employee.entity.Employee;
import com.leavemanagement.employee.exception.BusinessException;
import com.leavemanagement.employee.exception.ResourceNotFoundException;
import com.leavemanagement.employee.kafka.EmployeeEventProducer;
import com.leavemanagement.employee.repository.DepartmentRepository;
import com.leavemanagement.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - EmployeeService")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeEventProducer eventProducer;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private Department department;
    private EmployeeRequest request;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .name("Informatique")
                .build();

        employee = Employee.builder()
                .id(1L)
                .firstName("Ahmed")
                .lastName("Benali")
                .email("ahmed.benali@test.com")
                .jobTitle("Développeur")
                .hireDate(LocalDate.of(2023, 1, 15))
                .department(department)
                .build();

        request = new EmployeeRequest("Ahmed", "Benali", "ahmed.benali@test.com",
                "Développeur", LocalDate.of(2023, 1, 15), 1L);
    }

    @Test
    @DisplayName("getAllEmployees - retourne la liste des employés")
    void getAllEmployees_shouldReturnList() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("ahmed.benali@test.com");
    }

    @Test
    @DisplayName("getEmployeeById - retourne l'employé quand il existe")
    void getEmployeeById_whenExists_shouldReturn() {
        when(employeeRepository.findByIdWithDepartment(1L)).thenReturn(Optional.of(employee));

        EmployeeDTO result = employeeService.getEmployeeById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFullName()).isEqualTo("Ahmed Benali");
        assertThat(result.getDepartmentName()).isEqualTo("Informatique");
    }

    @Test
    @DisplayName("getEmployeeById - lève une exception quand il n'existe pas")
    void getEmployeeById_whenNotExists_shouldThrow() {
        when(employeeRepository.findByIdWithDepartment(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("createEmployee - crée avec succès et publie l'événement Kafka")
    void createEmployee_shouldCreateAndPublishEvent() {
        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        doNothing().when(eventProducer).publishEmployeeCreated(any());

        EmployeeDTO result = employeeService.createEmployee(request);

        assertThat(result.getEmail()).isEqualTo("ahmed.benali@test.com");
        verify(eventProducer).publishEmployeeCreated(any());
    }

    @Test
    @DisplayName("createEmployee - lève une exception si l'email existe déjà")
    void createEmployee_whenEmailExists_shouldThrow() {
        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("existe déjà");

        verify(employeeRepository, never()).save(any());
        verify(eventProducer, never()).publishEmployeeCreated(any());
    }

    @Test
    @DisplayName("deleteEmployee - supprime quand l'employé existe")
    void deleteEmployee_whenExists_shouldDelete() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteEmployee - lève exception quand l'employé n'existe pas")
    void deleteEmployee_whenNotExists_shouldThrow() {
        when(employeeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.deleteEmployee(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(employeeRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("assignDepartment - affecte un département à un employé")
    void assignDepartment_shouldWork() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDTO result = employeeService.assignDepartment(1L, 1L);

        assertThat(result.getDepartmentId()).isEqualTo(1L);
    }
}
