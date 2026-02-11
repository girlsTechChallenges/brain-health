package com.fiap.brain.health.domain.exception;

public class ArticleSearchException extends DomainException {

    public ArticleSearchException(String message) {
        super(message);
    }

    public ArticleSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
