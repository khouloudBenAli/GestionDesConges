package com.leavemanagement.leave.service;

import com.leavemanagement.leave.dto.InitBalanceRequest;
import com.leavemanagement.leave.dto.LeaveBalanceDTO;
import com.leavemanagement.leave.entity.LeaveBalance;
import com.leavemanagement.leave.entity.LeaveType;
import com.leavemanagement.leave.exception.BusinessException;
import com.leavemanagement.leave.exception.ResourceNotFoundException;
import com.leavemanagement.leave.repository.LeaveBalanceRepository;
import com.leavemanagement.leave.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public List<LeaveBalanceDTO> getEmployeeBalances(Long employeeId) {
        return leaveBalanceRepository.findByEmployeeId(employeeId).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<LeaveBalanceDTO> getEmployeeBalancesForYear(Long employeeId, int year) {
        return leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, year).stream()
                .map(this::toDTO)
                .toList();
    }

    public LeaveBalanceDTO getBalance(Long employeeId, Long leaveTypeId, int year) {
        return leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Solde introuvable pour employé=" + employeeId + ", type=" + leaveTypeId + ", année=" + year));
    }

    public boolean hasSufficientBalance(Long employeeId, Long leaveTypeId, int requestedDays) {
        int year = LocalDate.now().getYear();
        return leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
                .map(b -> b.hasSufficientBalance(requestedDays))
                .orElse(false);
    }

    @Transactional
    public LeaveBalanceDTO initializeBalance(InitBalanceRequest request) {
        if (leaveBalanceRepository.existsByEmployeeIdAndLeaveTypeIdAndYear(
                request.getEmployeeId(), request.getLeaveTypeId(), request.getYear())) {
            throw new BusinessException("Un solde existe déjà pour cet employé, ce type et cette année");
        }

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Type de congé", request.getLeaveTypeId()));

        LeaveBalance balance = LeaveBalance.builder()
                .employeeId(request.getEmployeeId())
                .leaveType(leaveType)
                .year(request.getYear())
                .allocatedDays(request.getAllocatedDays() > 0
                        ? request.getAllocatedDays()
                        : leaveType.getDefaultDaysPerYear())
                .totalDays(leaveType.getDefaultDaysPerYear())
                .usedDays(0)
                .carriedOverDays(0)
                .build();

        return toDTO(leaveBalanceRepository.save(balance));
    }

    @Transactional
    public void initializeBalancesForNewEmployee(Long employeeId) {
        int currentYear = LocalDate.now().getYear();
        List<LeaveType> activeTypes = leaveTypeRepository.findByActiveTrue();

        for (LeaveType leaveType : activeTypes) {
            if (!leaveBalanceRepository.existsByEmployeeIdAndLeaveTypeIdAndYear(
                    employeeId, leaveType.getId(), currentYear)) {

                LeaveBalance balance = LeaveBalance.builder()
                        .employeeId(employeeId)
                        .leaveType(leaveType)
                        .year(currentYear)
                        .totalDays(leaveType.getDefaultDaysPerYear())
                        .allocatedDays(leaveType.getDefaultDaysPerYear())
                        .usedDays(0)
                        .carriedOverDays(0)
                        .build();

                leaveBalanceRepository.save(balance);
                log.info("Solde initialisé pour employeeId={}, type={}, année={}",
                        employeeId, leaveType.getName(), currentYear);
            }
        }
    }

    @Transactional
    public void deductBalance(Long employeeId, Long leaveTypeId, int days) {
        int year = LocalDate.now().getYear();
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Solde introuvable pour l'approbation de la demande"));

        if (!balance.hasSufficientBalance(days)) {
            throw new com.leavemanagement.leave.exception.InsufficientBalanceException(
                    days, balance.getRemainingDays());
        }

        balance.setUsedDays(balance.getUsedDays() + days);
        leaveBalanceRepository.save(balance);
    }

    @Transactional
    public void restoreBalance(Long employeeId, Long leaveTypeId, int days) {
        int year = LocalDate.now().getYear();
        leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, year)
                .ifPresent(balance -> {
                    balance.setUsedDays(Math.max(0, balance.getUsedDays() - days));
                    leaveBalanceRepository.save(balance);
                });
    }

    private LeaveBalanceDTO toDTO(LeaveBalance b) {
        return LeaveBalanceDTO.builder()
                .id(b.getId())
                .employeeId(b.getEmployeeId())
                .leaveTypeId(b.getLeaveType().getId())
                .leaveTypeName(b.getLeaveType().getName())
                .leaveTypeColor(b.getLeaveType().getColorCode())
                .year(b.getYear())
                .totalDays(b.getTotalDays())
                .allocatedDays(b.getAllocatedDays())
                .usedDays(b.getUsedDays())
                .remainingDays(b.getRemainingDays())
                .carriedOverDays(b.getCarriedOverDays())
                .lastUpdated(b.getLastUpdated())
                .build();
    }
}
