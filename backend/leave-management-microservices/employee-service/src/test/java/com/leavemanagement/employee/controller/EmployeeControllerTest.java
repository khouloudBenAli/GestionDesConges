package com.leavemanagement.employee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leavemanagement.employee.dto.EmployeeDTO;
import com.leavemanagement.employee.dto.EmployeeRequest;
import com.leavemanagement.employee.exception.GlobalExceptionHandler;
import com.leavemanagement.employee.exception.ResourceNotFoundException;
import com.leavemanagement.employee.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Tests intégration - EmployeeController")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/employees - retourne 200 avec la liste")
    void getAllEmployees_shouldReturn200() throws Exception {
        EmployeeDTO dto = EmployeeDTO.builder()
                .id(1L)
                .firstName("Ahmed")
                .lastName("Benali")
                .fullName("Ahmed Benali")
                .email("ahmed@test.com")
                .build();

        when(employeeService.getAllEmployees()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ahmed@test.com"))
                .andExpect(jsonPath("$[0].fullName").value("Ahmed Benali"));
    }

    @Test
    @DisplayName("GET /api/employees/{id} - retourne 404 si l'employé n'existe pas")
    void getEmployeeById_whenNotFound_shouldReturn404() throws Exception {
        when(employeeService.getEmployeeById(99L))
                .thenThrow(new ResourceNotFoundException("Employé", 99L));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/employees - crée un employé et retourne 201")
    void createEmployee_shouldReturn201() throws Exception {
        EmployeeRequest request = new EmployeeRequest("Ahmed", "Benali",
                "ahmed@test.com", "Dev", LocalDate.of(2023, 1, 1), null);

        EmployeeDTO created = EmployeeDTO.builder()
                .id(1L)
                .firstName("Ahmed")
                .email("ahmed@test.com")
                .build();

        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/employees - retourne 400 si la validation échoue")
    void createEmployee_withInvalidData_shouldReturn400() throws Exception {
        EmployeeRequest invalid = new EmployeeRequest("", "", "not-an-email", null, null, null);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/employees/{id} - retourne 204 si supprimé")
    void deleteEmployee_shouldReturn204() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }
}
