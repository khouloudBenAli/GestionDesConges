package com.leavemanagement.leave.service;

import com.leavemanagement.leave.dto.CreateLeaveRequest;
import com.leavemanagement.leave.dto.LeaveActionRequest;
import com.leavemanagement.leave.dto.LeaveRequestDTO;
import com.leavemanagement.leave.entity.LeaveRequest;
import com.leavemanagement.leave.entity.LeaveStatus;
import com.leavemanagement.leave.entity.LeaveType;
import com.leavemanagement.leave.event.LeaveStatusChangedEvent;
import com.leavemanagement.leave.exception.BusinessException;
import com.leavemanagement.leave.exception.ResourceNotFoundException;
import com.leavemanagement.leave.kafka.LeaveEventProducer;
import com.leavemanagement.leave.repository.LeaveRequestRepository;
import com.leavemanagement.leave.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceService leaveBalanceService;
    private final LeaveEventProducer leaveEventProducer;

    public List<LeaveRequestDTO> getAllRequests() {
        return leaveRequestRepository.findAll().stream().map(this::toDTO).toList();
    }

    public LeaveRequestDTO getRequestById(Long id) {
        return leaveRequestRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de congé", id));
    }

    public List<LeaveRequestDTO> getRequestsByEmployee(Long employeeId) {
        return leaveRequestRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream().map(this::toDTO).toList();
    }

    public List<LeaveRequestDTO> getRequestsByStatus(LeaveStatus status) {
        return leaveRequestRepository.findByStatus(status).stream().map(this::toDTO).toList();
    }

    @Transactional
    public LeaveRequestDTO createRequest(CreateLeaveRequest dto) {
        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Type de congé", dto.getLeaveTypeId()));

        if (!leaveType.isActive()) {
            throw new BusinessException("Ce type de congé n'est pas actif");
        }

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("La date de fin ne peut pas être antérieure à la date de début");
        }

        // Vérification du délai de préavis
        if (leaveType.getMinNoticeDays() > 0) {
            long daysUntilStart = LocalDate.now().until(dto.getStartDate()).getDays();
            if (daysUntilStart < leaveType.getMinNoticeDays()) {
                throw new BusinessException("Un préavis de " + leaveType.getMinNoticeDays()
                        + " jours est requis pour ce type de congé");
            }
        }

        // Vérification des chevauchements
        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlappingApprovedRequests(
                dto.getEmployeeId(), dto.getStartDate(), dto.getEndDate());
        if (!overlapping.isEmpty()) {
            throw new BusinessException("Une demande de congé approuvée existe déjà sur cette période");
        }

        LeaveRequest request = LeaveRequest.builder()
                .employeeId(dto.getEmployeeId())
                .leaveType(leaveType)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .halfDayStart(dto.isHalfDayStart())
                .halfDayEnd(dto.isHalfDayEnd())
                .reason(dto.getReason())
                .isEmergency(dto.isEmergency())
                .contactInfo(dto.getContactInfo())
                .status(LeaveStatus.PENDING)
                .build();

        return toDTO(leaveRequestRepository.save(request));
    }

    @Transactional
    public LeaveRequestDTO approveRequest(Long id, LeaveActionRequest actionRequest) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de congé", id));

        if (!request.getStatus().canBeApproved()) {
            throw new BusinessException("Cette demande ne peut pas être approuvée (statut: "
                    + request.getStatus().getDisplayName() + ")");
        }

        // Vérification et déduction du solde
        leaveBalanceService.deductBalance(
                request.getEmployeeId(),
                request.getLeaveType().getId(),
                request.getNumberOfDays());

        request.setStatus(LeaveStatus.APPROVED);
        request.setManagerComment(actionRequest.getManagerComment());
        request.setResponseDate(LocalDateTime.now());

        LeaveRequest saved = leaveRequestRepository.save(request);

        leaveEventProducer.publishStatusChanged(LeaveStatusChangedEvent.builder()
                .leaveRequestId(saved.getId())
                .employeeId(saved.getEmployeeId())
                .newStatus(LeaveStatus.APPROVED.name())
                .leaveTypeName(saved.getLeaveType().getName())
                .startDate(saved.getStartDate())
                .endDate(saved.getEndDate())
                .numberOfDays(saved.getNumberOfDays())
                .build());

        return toDTO(saved);
    }

    @Transactional
    public LeaveRequestDTO rejectRequest(Long id, LeaveActionRequest actionRequest) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de congé", id));

        if (!request.getStatus().canBeRejected()) {
            throw new BusinessException("Cette demande ne peut pas être rejetée");
        }

        if (actionRequest.getManagerComment() == null || actionRequest.getManagerComment().isBlank()) {
            throw new BusinessException("Un commentaire est obligatoire pour le rejet");
        }

        request.setStatus(LeaveStatus.REJECTED);
        request.setManagerComment(actionRequest.getManagerComment());
        request.setResponseDate(LocalDateTime.now());

        LeaveRequest saved = leaveRequestRepository.save(request);

        leaveEventProducer.publishStatusChanged(LeaveStatusChangedEvent.builder()
                .leaveRequestId(saved.getId())
                .employeeId(saved.getEmployeeId())
                .newStatus(LeaveStatus.REJECTED.name())
                .leaveTypeName(saved.getLeaveType().getName())
                .startDate(saved.getStartDate())
                .endDate(saved.getEndDate())
                .numberOfDays(saved.getNumberOfDays())
                .build());

        return toDTO(saved);
    }

    @Transactional
    public LeaveRequestDTO cancelRequest(Long id) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de congé", id));

        if (!request.getStatus().canBeCancelled()) {
            throw new BusinessException("Cette demande ne peut pas être annulée (statut: "
                    + request.getStatus().getDisplayName() + ")");
        }

        // Si la demande était approuvée, restaurer le solde
        if (request.getStatus() == LeaveStatus.APPROVED) {
            leaveBalanceService.restoreBalance(
                    request.getEmployeeId(),
                    request.getLeaveType().getId(),
                    request.getNumberOfDays());
        }

        request.setStatus(LeaveStatus.CANCELLED);
        request.setResponseDate(LocalDateTime.now());

        return toDTO(leaveRequestRepository.save(request));
    }

    private LeaveRequestDTO toDTO(LeaveRequest r) {
        return LeaveRequestDTO.builder()
                .id(r.getId())
                .employeeId(r.getEmployeeId())
                .leaveTypeId(r.getLeaveType().getId())
                .leaveTypeName(r.getLeaveType().getName())
                .leaveTypeColor(r.getLeaveType().getColorCode())
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .halfDayStart(r.isHalfDayStart())
                .halfDayEnd(r.isHalfDayEnd())
                .reason(r.getReason())
                .isEmergency(r.isEmergency())
                .contactInfo(r.getContactInfo())
                .status(r.getStatus())
                .statusDisplay(r.getStatus().getDisplayName())
                .numberOfDays(r.getNumberOfDays())
                .managerComment(r.getManagerComment())
                .createdAt(r.getCreatedAt())
                .responseDate(r.getResponseDate())
                .build();
    }
}
