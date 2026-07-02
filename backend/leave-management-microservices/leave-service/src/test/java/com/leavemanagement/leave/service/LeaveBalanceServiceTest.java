package com.leavemanagement.leave.service;

import com.leavemanagement.leave.dto.InitBalanceRequest;
import com.leavemanagement.leave.dto.LeaveBalanceDTO;
import com.leavemanagement.leave.entity.LeaveBalance;
import com.leavemanagement.leave.entity.LeaveType;
import com.leavemanagement.leave.exception.BusinessException;
import com.leavemanagement.leave.exception.InsufficientBalanceException;
import com.leavemanagement.leave.repository.LeaveBalanceRepository;
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
@DisplayName("Tests unitaires - LeaveBalanceService")
class LeaveBalanceServiceTest {

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @InjectMocks
    private LeaveBalanceService leaveBalanceService;

    private LeaveType leaveType;
    private LeaveBalance balance;

    @BeforeEach
    void setUp() {
        leaveType = LeaveType.builder()
                .id(1L)
                .name("Congé annuel")
                .defaultDaysPerYear(25)
                .active(true)
                .build();

        balance = LeaveBalance.builder()
                .id(1L)
                .employeeId(10L)
                .leaveType(leaveType)
                .year(LocalDate.now().getYear())
                .totalDays(25)
                .allocatedDays(25)
                .usedDays(5)
                .carriedOverDays(0)
                .build();
    }

    @Test
    @DisplayName("getEmployeeBalances - retourne les soldes d'un employé")
    void getEmployeeBalances_shouldReturn() {
        when(leaveBalanceRepository.findByEmployeeId(10L)).thenReturn(List.of(balance));

        List<LeaveBalanceDTO> result = leaveBalanceService.getEmployeeBalances(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRemainingDays()).isEqualTo(20); // 25 - 5
    }

    @Test
    @DisplayName("initializeBalance - crée un nouveau solde")
    void initializeBalance_shouldCreate() {
        InitBalanceRequest request = new InitBalanceRequest(10L, 1L, LocalDate.now().getYear(), 0);
        when(leaveBalanceRepository.existsByEmployeeIdAndLeaveTypeIdAndYear(10L, 1L, LocalDate.now().getYear()))
                .thenReturn(false);
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(balance);

        LeaveBalanceDTO result = leaveBalanceService.initializeBalance(request);

        assertThat(result.getEmployeeId()).isEqualTo(10L);
        verify(leaveBalanceRepository).save(any(LeaveBalance.class));
    }

    @Test
    @DisplayName("initializeBalance - lève exception si solde déjà existant")
    void initializeBalance_whenAlreadyExists_shouldThrow() {
        InitBalanceRequest request = new InitBalanceRequest(10L, 1L, LocalDate.now().getYear(), 0);
        when(leaveBalanceRepository.existsByEmployeeIdAndLeaveTypeIdAndYear(10L, 1L, LocalDate.now().getYear()))
                .thenReturn(true);

        assertThatThrownBy(() -> leaveBalanceService.initializeBalance(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("existe déjà");
    }

    @Test
    @DisplayName("deductBalance - déduit les jours utilisés")
    void deductBalance_shouldDeductDays() {
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(10L, 1L, LocalDate.now().getYear()))
                .thenReturn(Optional.of(balance));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(balance);

        leaveBalanceService.deductBalance(10L, 1L, 3);

        assertThat(balance.getUsedDays()).isEqualTo(8); // 5 + 3
    }

    @Test
    @DisplayName("deductBalance - lève InsufficientBalanceException si solde insuffisant")
    void deductBalance_whenInsufficient_shouldThrow() {
        balance.setUsedDays(23); // 25 - 23 = 2 jours restants
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(10L, 1L, LocalDate.now().getYear()))
                .thenReturn(Optional.of(balance));

        assertThatThrownBy(() -> leaveBalanceService.deductBalance(10L, 1L, 5))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Solde insuffisant");
    }

    @Test
    @DisplayName("restoreBalance - restaure les jours après annulation")
    void restoreBalance_shouldRestoreDays() {
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(10L, 1L, LocalDate.now().getYear()))
                .thenReturn(Optional.of(balance));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(balance);

        leaveBalanceService.restoreBalance(10L, 1L, 3);

        assertThat(balance.getUsedDays()).isEqualTo(2); // 5 - 3
    }

    @Test
    @DisplayName("initializeBalancesForNewEmployee - crée tous les soldes actifs")
    void initializeBalancesForNewEmployee_shouldCreateAllActiveTypes() {
        LeaveType type2 = LeaveType.builder().id(2L).name("Maladie").defaultDaysPerYear(10).active(true).build();
        when(leaveTypeRepository.findByActiveTrue()).thenReturn(List.of(leaveType, type2));
        when(leaveBalanceRepository.existsByEmployeeIdAndLeaveTypeIdAndYear(anyLong(), anyLong(), anyInt()))
                .thenReturn(false);
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(balance);

        leaveBalanceService.initializeBalancesForNewEmployee(10L);

        verify(leaveBalanceRepository, times(2)).save(any(LeaveBalance.class));
    }
}
