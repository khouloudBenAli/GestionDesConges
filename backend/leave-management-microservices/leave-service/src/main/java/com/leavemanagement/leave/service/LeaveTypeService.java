package com.leavemanagement.leave.service;

import com.leavemanagement.leave.dto.LeaveTypeDTO;
import com.leavemanagement.leave.dto.LeaveTypeRequest;
import com.leavemanagement.leave.entity.LeaveType;
import com.leavemanagement.leave.exception.BusinessException;
import com.leavemanagement.leave.exception.ResourceNotFoundException;
import com.leavemanagement.leave.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    public List<LeaveTypeDTO> getAllLeaveTypes() {
        return leaveTypeRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<LeaveTypeDTO> getActiveLeaveTypes() {
        return leaveTypeRepository.findByActiveTrue().stream().map(this::toDTO).toList();
    }

    public LeaveTypeDTO getLeaveTypeById(Long id) {
        return leaveTypeRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Type de congé", id));
    }

    @Transactional
    public LeaveTypeDTO createLeaveType(LeaveTypeRequest request) {
        if (leaveTypeRepository.existsByName(request.getName())) {
            throw new BusinessException("Un type de congé '" + request.getName() + "' existe déjà");
        }
        return toDTO(leaveTypeRepository.save(fromRequest(request)));
    }

    @Transactional
    public LeaveTypeDTO updateLeaveType(Long id, LeaveTypeRequest request) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de congé", id));

        if (leaveTypeRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BusinessException("Un type de congé '" + request.getName() + "' existe déjà");
        }

        leaveType.setName(request.getName());
        leaveType.setDescription(request.getDescription());
        leaveType.setPaidLeave(request.isPaidLeave());
        leaveType.setDefaultDaysPerYear(request.getDefaultDaysPerYear());
        leaveType.setColorCode(request.getColorCode());
        leaveType.setRequiresDocumentation(request.isRequiresDocumentation());
        leaveType.setCanCarryOver(request.isCanCarryOver());
        leaveType.setMaxCarryOverDays(request.getMaxCarryOverDays());
        leaveType.setMinNoticeDays(request.getMinNoticeDays());
        leaveType.setAllowHalfDay(request.isAllowHalfDay());
        leaveType.setActive(request.isActive());

        return toDTO(leaveTypeRepository.save(leaveType));
    }

    @Transactional
    public void deleteLeaveType(Long id) {
        if (!leaveTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Type de congé", id);
        }
        leaveTypeRepository.deleteById(id);
    }

    private LeaveType fromRequest(LeaveTypeRequest req) {
        return LeaveType.builder()
                .name(req.getName())
                .description(req.getDescription())
                .paidLeave(req.isPaidLeave())
                .defaultDaysPerYear(req.getDefaultDaysPerYear())
                .colorCode(req.getColorCode())
                .requiresDocumentation(req.isRequiresDocumentation())
                .canCarryOver(req.isCanCarryOver())
                .maxCarryOverDays(req.getMaxCarryOverDays())
                .minNoticeDays(req.getMinNoticeDays())
                .allowHalfDay(req.isAllowHalfDay())
                .active(req.isActive())
                .build();
    }

    public LeaveTypeDTO toDTO(LeaveType lt) {
        return LeaveTypeDTO.builder()
                .id(lt.getId())
                .name(lt.getName())
                .description(lt.getDescription())
                .paidLeave(lt.isPaidLeave())
                .defaultDaysPerYear(lt.getDefaultDaysPerYear())
                .colorCode(lt.getColorCode())
                .requiresDocumentation(lt.isRequiresDocumentation())
                .canCarryOver(lt.isCanCarryOver())
                .maxCarryOverDays(lt.getMaxCarryOverDays())
                .minNoticeDays(lt.getMinNoticeDays())
                .allowHalfDay(lt.isAllowHalfDay())
                .active(lt.isActive())
                .build();
    }
}
