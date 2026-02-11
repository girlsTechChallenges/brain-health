package com.fiap.brain.health.api.dto.request;

import java.util.Objects;

public record AIArticleRequest(String message) {

    public AIArticleRequest {
        Objects.requireNonNull(message, "Message cannot be null");
        if (message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be blank");
        }
    }
}
