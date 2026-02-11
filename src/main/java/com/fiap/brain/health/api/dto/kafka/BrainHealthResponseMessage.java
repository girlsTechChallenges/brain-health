package com.fiap.brain.health.api.dto.kafka;

import com.fiap.brain.health.api.dto.response.ArticleResponse;

import java.time.LocalDateTime;

public record BrainHealthResponseMessage(
        String messageId,
        String userId,
        String correlationId,
        ArticleResponse articleResponse,
        ProcessingStatus status,
        String errorMessage,
        LocalDateTime processedAt
) {
    public enum ProcessingStatus {
        SUCCESS,
        FAILED,
    }

    public BrainHealthResponseMessage {
        if (messageId == null || messageId.isBlank()) {
            throw new IllegalArgumentException("messageId is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("status is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String messageId;
        private String userId;
        private String correlationId;
        private ArticleResponse articleResponse;
        private ProcessingStatus status;
        private String errorMessage;
        private LocalDateTime processedAt;

        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder articleResponse(ArticleResponse articleResponse) {
            this.articleResponse = articleResponse;
            return this;
        }

        public Builder status(ProcessingStatus status) {
            this.status = status;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder processedAt(LocalDateTime processedAt) {
            this.processedAt = processedAt;
            return this;
        }

        public BrainHealthResponseMessage build() {
            return new BrainHealthResponseMessage(
                    messageId,
                    userId,
                    correlationId,
                    articleResponse,
                    status,
                    errorMessage,
                    processedAt
            );
        }
    }
}
