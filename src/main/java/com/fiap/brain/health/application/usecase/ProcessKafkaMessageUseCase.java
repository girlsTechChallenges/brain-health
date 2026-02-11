package com.fiap.brain.health.application.usecase;

import com.fiap.brain.health.api.dto.kafka.BrainHealthRequestMessage;
import com.fiap.brain.health.api.dto.kafka.BrainHealthResponseMessage;
import com.fiap.brain.health.application.mapper.ArticleResponseMapper;
import com.fiap.brain.health.domain.model.MedicalArticle;
import com.fiap.brain.health.domain.port.AIProcessingPort;
import com.fiap.brain.health.domain.port.MedicalArticleRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessKafkaMessageUseCase {

    private final MedicalArticleRepositoryPort articleRepository;
    private final AIProcessingPort aiProcessing;
    private final ArticleResponseMapper responseMapper;

    public BrainHealthResponseMessage process(BrainHealthRequestMessage request) {
        log.info("Processing Kafka message - messageId: {}, correlationId: {}",
                request.messageId(), request.correlationId());

        try {
            MedicalArticle article = articleRepository.findByTopic(request.question())
                    .orElseThrow(() -> new ArticleNotFoundException(request.question()));

            if (!article.hasMinimumContent(100)) {
                log.warn("Article content too short for question: {}", request.question());
                return buildErrorResponse(request, "Conteúdo do artigo insuficiente");
            }

            AIProcessingPort.AIProcessingResult aiResult =
                    aiProcessing.processArticle(request.question(), article);

            var articleResponse = responseMapper.toArticleResponse(aiResult, article);

            return buildSuccessResponse(request, articleResponse);

        } catch (ArticleNotFoundException e) {
            log.warn("Article not found for Kafka message: {}", e.getMessage());
            return buildErrorResponse(request, "Artigo não encontrado: " + e.getMessage());

        } catch (AIProcessingPort.AIProcessingException e) {
            log.error("AI processing failed for Kafka message: {}", e.getMessage(), e);
            return buildErrorResponse(request, "Erro ao processar com IA: " + e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected error processing Kafka message: {}", e.getMessage(), e);
            return buildErrorResponse(request, "Erro inesperado: " + e.getMessage());
        }
    }

    private BrainHealthResponseMessage buildSuccessResponse(
            BrainHealthRequestMessage request,
            com.fiap.brain.health.api.dto.response.ArticleResponse articleResponse) {

        return BrainHealthResponseMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .userId(request.userId())
                .correlationId(request.correlationId())
                .articleResponse(articleResponse)
                .status(BrainHealthResponseMessage.ProcessingStatus.SUCCESS)
                .processedAt(LocalDateTime.now())
                .build();
    }

    private BrainHealthResponseMessage buildErrorResponse(
            BrainHealthRequestMessage request,
            String errorMessage) {

        return BrainHealthResponseMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .userId(request.userId())
                .correlationId(request.correlationId())
                .status(BrainHealthResponseMessage.ProcessingStatus.FAILED)
                .errorMessage(errorMessage)
                .processedAt(LocalDateTime.now())
                .build();
    }

    public String resolveKey(BrainHealthRequestMessage request) {
        return Optional.ofNullable(request.correlationId())
                .orElse(request.userId());
    }

    private static class ArticleNotFoundException extends RuntimeException {
        public ArticleNotFoundException(String question) {
            super(String.format("No article found for question: %s", question));
        }
    }
}
