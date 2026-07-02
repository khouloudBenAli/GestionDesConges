package com.leavemanagement.leave.kafka;

import com.leavemanagement.leave.event.EmployeeCreatedEvent;
import com.leavemanagement.leave.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeEventConsumer {

    private final LeaveBalanceService leaveBalanceService;

    @KafkaListener(
            topics = "employee-events",
            groupId = "leave-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("Événement reçu: employee-created pour employeeId={} ({})",
                event.getEmployeeId(), event.getEmail());
        try {
            leaveBalanceService.initializeBalancesForNewEmployee(event.getEmployeeId());
            log.info("Soldes de congés initialisés pour employeeId={}", event.getEmployeeId());
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation des soldes pour employeeId={}",
                    event.getEmployeeId(), e);
        }
    }
}
