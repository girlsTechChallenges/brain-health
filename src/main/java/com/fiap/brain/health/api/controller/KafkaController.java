package com.fiap.brain.health.api.controller;

import com.fiap.brain.health.api.dto.kafka.BrainHealthRequestMessage;
import com.fiap.brain.health.api.dto.request.KafkaTestMessageRequest;
import com.fiap.brain.health.infrastructure.adapter.kafka.BrainHealthKafkaProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/kafka")
@RequiredArgsConstructor
@Tag(name = "Kafka Testing", description = "Endpoints for testing Kafka integration")
public class KafkaController {

    private final BrainHealthKafkaProducer kafkaProducer;

    @PostMapping("/test/send")
    @Operation(summary = "Send test message to Kafka",
               description = "Posts a test message to brain-health-request topic")
    public ResponseEntity<Map<String, Object>> sendTestMessage(
            @RequestBody KafkaTestMessageRequest request) {

        log.info("Sending test message to Kafka - question: {}", request.question());

        String messageId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();

        BrainHealthRequestMessage kafkaMessage = BrainHealthRequestMessage.builder()
                .messageId(messageId)
                .userId(request.userId() != null ? request.userId() : "test-user")
                .question(request.question())
                .category(request.category())
                .correlationId(correlationId)
                .requestedAt(LocalDateTime.now())
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "TEST_MODE");
        response.put("message", "Message structure created (producer sends to response topic)");
        response.put("messageId", messageId);
        response.put("correlationId", correlationId);
        response.put("question", request.question());
        response.put("note", "To test full flow, use kafka-console-producer to send to brain-health-request topic");

        log.info("Test message created - messageId: {}, correlationId: {}", messageId, correlationId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    @Operation(summary = "Get Kafka configuration info")
    public ResponseEntity<Map<String, String>> getKafkaInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("requestTopic", "brain-health-request");
        info.put("responseTopic", "brain-health-response");
        info.put("status", "Kafka integration active");

        return ResponseEntity.ok(info);
    }
}
