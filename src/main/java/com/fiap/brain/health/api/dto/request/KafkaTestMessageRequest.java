package com.fiap.brain.health.api.dto.request;

public record KafkaTestMessageRequest(
        String question,
        String userId,
        String category
) {
    public KafkaTestMessageRequest {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question is required");
        }
    }
}
