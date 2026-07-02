package com.leavemanagement.leave.kafka;

import com.leavemanagement.leave.event.LeaveStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveEventProducer {

    private static final String TOPIC = "leave-events";

    private final KafkaTemplate<String, LeaveStatusChangedEvent> kafkaTemplate;

    public void publishStatusChanged(LeaveStatusChangedEvent event) {
        log.info("Publication leave-status-changed: requestId={}, status={}",
                event.getLeaveRequestId(), event.getNewStatus());
        kafkaTemplate.send(TOPIC, String.valueOf(event.getLeaveRequestId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Échec publication leave-event pour requestId={}", event.getLeaveRequestId(), ex);
                    }
                });
    }
}
