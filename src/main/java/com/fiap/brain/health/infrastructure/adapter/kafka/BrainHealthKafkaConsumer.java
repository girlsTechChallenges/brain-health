package com.fiap.brain.health.infrastructure.adapter.kafka;

import com.fiap.brain.health.api.dto.kafka.BrainHealthRequestMessage;
import com.fiap.brain.health.api.dto.kafka.BrainHealthResponseMessage;
import com.fiap.brain.health.application.usecase.ProcessKafkaMessageUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BrainHealthKafkaConsumer {

    private final ProcessKafkaMessageUseCase processKafkaMessageUseCase;
    private final BrainHealthKafkaProducer kafkaProducer;

    @KafkaListener(
            topics = "${kafka.topic.brain-health-request}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            @Payload BrainHealthRequestMessage message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        log.info("Kafka message received - partition: {}, offset: {}, messageId: {}, correlationId: {}",
                partition, offset, message.messageId(), message.correlationId());

        try {
            validateMessage(message);

            BrainHealthResponseMessage response = processKafkaMessageUseCase.process(message);
            String key = processKafkaMessageUseCase.resolveKey(message);
            kafkaProducer.sendResponse(key, response);

            acknowledgment.acknowledge();

            log.info("Kafka message processed - messageId: {}, status: {}",
                    message.messageId(), response.status());

        } catch (IllegalArgumentException e) {
            log.error("Validation error - messageId: {}, error: {}", message.messageId(), e.getMessage());

            BrainHealthResponseMessage errorResponse = buildValidationErrorResponse(message, e.getMessage());
            kafkaProducer.sendResponse(processKafkaMessageUseCase.resolveKey(message), errorResponse);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Processing error - messageId: {}, error: {}", message.messageId(), e.getMessage(), e);

            BrainHealthResponseMessage errorResponse = buildProcessingErrorResponse(message, e.getMessage());
            kafkaProducer.sendResponse(processKafkaMessageUseCase.resolveKey(message), errorResponse);
            acknowledgment.acknowledge();
        }
    }

    private void validateMessage(BrainHealthRequestMessage message) {
        if (message.messageId() == null || message.messageId().isBlank()) {
            throw new IllegalArgumentException("messageId is required");
        }
        if (message.question() == null || message.question().isBlank()) {
            throw new IllegalArgumentException("question is required");
        }
        if (message.userId() == null || message.userId().isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
    }

    private BrainHealthResponseMessage buildValidationErrorResponse(
            BrainHealthRequestMessage request, String errorMessage) {
        return BrainHealthResponseMessage.builder()
                .messageId(java.util.UUID.randomUUID().toString())
                .userId(request.userId())
                .correlationId(request.correlationId())
                .status(BrainHealthResponseMessage.ProcessingStatus.FAILED)
                .errorMessage("Validation error: " + errorMessage)
                .processedAt(java.time.LocalDateTime.now())
                .build();
    }

    private BrainHealthResponseMessage buildProcessingErrorResponse(
            BrainHealthRequestMessage request, String errorMessage) {
        return BrainHealthResponseMessage.builder()
                .messageId(java.util.UUID.randomUUID().toString())
                .userId(request.userId())
                .correlationId(request.correlationId())
                .status(BrainHealthResponseMessage.ProcessingStatus.FAILED)
                .errorMessage("Processing error: " + errorMessage)
                .processedAt(java.time.LocalDateTime.now())
                .build();
    }
}
