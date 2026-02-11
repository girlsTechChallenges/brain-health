package com.fiap.brain.health.domain.exception;

public class ArticleNotFoundException extends DomainException {

    public ArticleNotFoundException(String message) {
        super(message);
    }

    public ArticleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ArticleNotFoundException forTopic(String topic) {
        return new ArticleNotFoundException(
            String.format("No article found for topic: %s", topic)
        );
    }
}
