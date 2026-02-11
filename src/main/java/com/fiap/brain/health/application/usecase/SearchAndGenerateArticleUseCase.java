package com.fiap.brain.health.application.usecase;

import com.fiap.brain.health.domain.model.MedicalArticle;
import com.fiap.brain.health.domain.port.AIProcessingPort;
import com.fiap.brain.health.domain.port.MedicalArticleRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchAndGenerateArticleUseCase {

    private final MedicalArticleRepositoryPort articleRepository;
    private final AIProcessingPort aiProcessing;

    public AIProcessingPort.AIProcessingResult execute(String question) {
        log.info("Executing use case: Search and Generate Article - Question: {}", question);

        try {
            MedicalArticle article = articleRepository.findByTopic(question)
                    .orElseThrow(() -> new ArticleNotFoundForTopicException(question));

            if (!article.hasMinimumContent(100)) {
                log.warn("Article content too short: {} characters", article.getContentLength());
                throw new InsufficientArticleContentException(article.getContentLength());
            }
            if (!article.isFromTrustedSource()) {
                log.warn("Article from untrusted source: {}", article.getArticleUrl());
            }
            AIProcessingPort.AIProcessingResult result = aiProcessing.processArticle(question, article);

            log.info("Use case completed successfully - Title: {}", result.title());
            return result;

        } catch (ArticleNotFoundForTopicException e) {
            log.warn("Article not found for topic: {}", question);
            throw e;
        } catch (InsufficientArticleContentException e) {
            log.error("Insufficient article content: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in use case: {}", e.getMessage(), e);
            throw new UseCaseExecutionException("Failed to execute article generation use case", e);
        }
    }

    public static class ArticleNotFoundForTopicException extends RuntimeException {
        public ArticleNotFoundForTopicException(String topic) {
            super(String.format("No medical article found for topic: %s", topic));
        }
    }

    public static class InsufficientArticleContentException extends RuntimeException {
        public InsufficientArticleContentException(int actualLength) {
            super(String.format("Article content too short. Length: %d characters (minimum: 100)", actualLength));
        }
    }

    public static class UseCaseExecutionException extends RuntimeException {
        public UseCaseExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
