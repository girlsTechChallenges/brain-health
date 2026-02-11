package com.fiap.brain.health.api.dto.kafka;

import java.time.LocalDateTime;

public record BrainHealthRequestMessage(
        String messageId,
        String userId,
        String question,
        String category,
        LocalDateTime requestedAt,
        String correlationId
) {
    public BrainHealthRequestMessage {
        if (messageId == null || messageId.isBlank()) {
            throw new IllegalArgumentException("messageId is required");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("question is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String messageId;
        private String userId;
        private String question;
        private String category;
        private LocalDateTime requestedAt;
        private String correlationId;

        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder question(String question) {
            this.question = question;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder requestedAt(LocalDateTime requestedAt) {
            this.requestedAt = requestedAt;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public BrainHealthRequestMessage build() {
            return new BrainHealthRequestMessage(
                    messageId,
                    userId,
                    question,
                    category,
                    requestedAt,
                    correlationId
            );
        }
    }
}
