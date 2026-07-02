package com.leavemanagement.leave.service;

import com.leavemanagement.leave.dto.CreateLeaveRequest;
import com.leavemanagement.leave.dto.LeaveActionRequest;
import com.leavemanagement.leave.dto.LeaveRequestDTO;
import com.leavemanagement.leave.entity.LeaveRequest;
import com.leavemanagement.leave.entity.LeaveStatus;
import com.leavemanagement.leave.entity.LeaveType;
import com.leavemanagement.leave.exception.BusinessException;
import com.leavemanagement.leave.exception.ResourceNotFoundException;
import com.leavemanagement.leave.kafka.LeaveEventProducer;
import com.leavemanagement.leave.repository.LeaveRequestRepository;
import com.leavemanagement.leave.repository.LeaveTypeRepository;
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
@DisplayName("Tests unitaires - LeaveRequestService")
class LeaveRequestServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private LeaveBalanceService leaveBalanceService;

    @Mock
    private LeaveEventProducer leaveEventProducer;

    @InjectMocks
    private LeaveRequestService leaveRequestService;

    private LeaveType leaveType;
    private LeaveRequest leaveRequest;
    private CreateLeaveRequest createRequest;

    @BeforeEach
    void setUp() {
        leaveType = LeaveType.builder()
                .id(1L)
                .name("Congé annuel")
                .defaultDaysPerYear(25)
                .minNoticeDays(0)
                .active(true)
                .allowHalfDay(true)
                .build();

        leaveRequest = LeaveRequest.builder()
                .id(1L)
                .employeeId(10L)
                .leaveType(leaveType)
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(7))
                .status(LeaveStatus.PENDING)
                .numberOfDays(3)
                .build();

        createRequest = new CreateLeaveRequest(
                10L, 1L,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(7),
                false, false,
                "Vacances", false, null);
    }

    @Test
    @DisplayName("createRequest - crée avec succès sans chevauchement")
    void createRequest_shouldSucceed() {
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveRequestRepository.findOverlappingApprovedRequests(any(), any(), any())).thenReturn(List.of());
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        LeaveRequestDTO result = leaveRequestService.createRequest(createRequest);

        assertThat(result.getEmployeeId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo(LeaveStatus.PENDING);
        verify(leaveRequestRepository).save(any(LeaveRequest.class));
    }

    @Test
    @DisplayName("createRequest - lève exception si le type de congé n'existe pas")
    void createRequest_whenLeaveTypeNotFound_shouldThrow() {
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaveRequestService.createRequest(createRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("createRequest - lève exception si le type est inactif")
    void createRequest_whenLeaveTypeInactive_shouldThrow() {
        leaveType.setActive(false);
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));

        assertThatThrownBy(() -> leaveRequestService.createRequest(createRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("actif");
    }

    @Test
    @DisplayName("createRequest - lève exception si dates invalides (fin avant début)")
    void createRequest_whenEndBeforeStart_shouldThrow() {
        createRequest.setEndDate(LocalDate.now().plusDays(2));
        createRequest.setStartDate(LocalDate.now().plusDays(5));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));

        assertThatThrownBy(() -> leaveRequestService.createRequest(createRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("date de fin");
    }

    @Test
    @DisplayName("createRequest - lève exception si chevauchement avec une demande approuvée")
    void createRequest_whenOverlap_shouldThrow() {
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveRequestRepository.findOverlappingApprovedRequests(any(), any(), any()))
                .thenReturn(List.of(leaveRequest));

        assertThatThrownBy(() -> leaveRequestService.createRequest(createRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("existe déjà sur cette période");
    }

    @Test
    @DisplayName("approveRequest - approuve et déduit le solde")
    void approveRequest_shouldApproveAndDeductBalance() {
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        doNothing().when(leaveBalanceService).deductBalance(anyLong(), anyLong(), anyInt());
        when(leaveRequestRepository.save(any())).thenReturn(leaveRequest);
        doNothing().when(leaveEventProducer).publishStatusChanged(any());

        leaveRequestService.approveRequest(1L, new LeaveActionRequest("Approuvé"));

        verify(leaveBalanceService).deductBalance(10L, 1L, 3);
        verify(leaveEventProducer).publishStatusChanged(any());
    }

    @Test
    @DisplayName("rejectRequest - lève exception si commentaire vide")
    void rejectRequest_withoutComment_shouldThrow() {
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        assertThatThrownBy(() -> leaveRequestService.rejectRequest(1L, new LeaveActionRequest("")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("commentaire");
    }

    @Test
    @DisplayName("cancelRequest - restaure le solde si la demande était approuvée")
    void cancelRequest_whenApproved_shouldRestoreBalance() {
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        doNothing().when(leaveBalanceService).restoreBalance(anyLong(), anyLong(), anyInt());
        when(leaveRequestRepository.save(any())).thenReturn(leaveRequest);

        leaveRequestService.cancelRequest(1L);

        verify(leaveBalanceService).restoreBalance(10L, 1L, 3);
    }

    @Test
    @DisplayName("cancelRequest - lève exception si la demande est définitivement terminée")
    void cancelRequest_whenRejected_shouldThrow() {
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        assertThatThrownBy(() -> leaveRequestService.cancelRequest(1L))
                .isInstanceOf(BusinessException.class);
    }
}
