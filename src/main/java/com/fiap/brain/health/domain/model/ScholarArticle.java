package com.fiap.brain.health.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * Value Object que encapsula o resultado da busca de artigo no Google Scholar.
 * Contém o conteúdo do artigo e a URL original.
 */
@Getter
@AllArgsConstructor
public class ScholarArticle {
    private final String content;
    private final String articleUrl;

    public static Optional<ScholarArticle> of(String content, String articleUrl) {
        if (content == null || content.isBlank() || articleUrl == null || articleUrl.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new ScholarArticle(content, articleUrl));
    }

    public static Optional<ScholarArticle> empty() {
        return Optional.empty();
    }
}
