package com.fiap.brain.health.api.controller;

import com.fiap.brain.health.api.dto.request.AIArticleRequest;
import com.fiap.brain.health.api.dto.response.ArticleResponse;
import com.fiap.brain.health.application.mapper.ArticleResponseMapper;
import com.fiap.brain.health.application.usecase.SearchAndGenerateArticleUseCase;
import com.fiap.brain.health.domain.model.MedicalArticle;
import com.fiap.brain.health.domain.port.AIProcessingPort;
import com.fiap.brain.health.domain.port.MedicalArticleRepositoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai/articles")
@RequiredArgsConstructor
@Validated
@Tag(name = "AI Articles", description = "AI-powered medical article generation")
public class AIArticleController {

    private final SearchAndGenerateArticleUseCase searchAndGenerateUseCase;
    private final ArticleResponseMapper responseMapper;
    private final MedicalArticleRepositoryPort articleRepository;

    @PostMapping("/search")
    @Operation(summary = "Search and generate AI article",
            description = "Searches medical articles and generates structured content with AI")
    public ResponseEntity<ArticleResponse> searchArticle(
            @RequestBody @Validated AIArticleRequest request) {

        log.info("Received article search request: {}", request.message());

        try {
            AIProcessingPort.AIProcessingResult aiResult =
                    searchAndGenerateUseCase.execute(request.message());

            MedicalArticle article = articleRepository.findByTopic(request.message())
                    .orElseThrow();

            ArticleResponse response = responseMapper.toArticleResponse(aiResult, article);

            log.info("Article search completed: {}", response.title());

            return ResponseEntity.ok(response);

        } catch (SearchAndGenerateArticleUseCase.ArticleNotFoundForTopicException e) {
            log.warn("Article not found: {}", e.getMessage());
            return ResponseEntity.ok(responseMapper.toNotFoundResponse());

        } catch (SearchAndGenerateArticleUseCase.InsufficientArticleContentException e) {
            log.error("Insufficient content: {}", e.getMessage());
            return ResponseEntity.ok(responseMapper.toErrorResponse(e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.ok(responseMapper.toErrorResponse("Erro inesperado ao processar requisição"));
        }
    }
}
