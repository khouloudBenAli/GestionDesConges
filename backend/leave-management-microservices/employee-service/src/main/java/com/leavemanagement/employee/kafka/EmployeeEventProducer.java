package com.leavemanagement.employee.kafka;

import com.leavemanagement.employee.event.EmployeeCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeEventProducer {

    private static final String TOPIC = "employee-events";

    private final KafkaTemplate<String, EmployeeCreatedEvent> kafkaTemplate;

    public void publishEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("Publication de l'événement employee-created pour employeeId={}", event.getEmployeeId());
        kafkaTemplate.send(TOPIC, String.valueOf(event.getEmployeeId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Échec de publication de l'événement pour employeeId={}", event.getEmployeeId(), ex);
                    } else {
                        log.debug("Événement publié avec succès: partition={}, offset={}",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
