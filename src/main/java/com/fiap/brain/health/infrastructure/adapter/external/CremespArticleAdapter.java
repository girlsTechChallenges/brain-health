package com.fiap.brain.health.infrastructure.adapter.external;

import com.fiap.brain.health.domain.model.MedicalArticle;
import com.fiap.brain.health.domain.port.MedicalArticleRepositoryPort;
import com.fiap.brain.health.infrastructure.adapter.html.HtmlFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * INFRASTRUCTURE ADAPTER: CREMESP Article Repository
 *
 * Implements MedicalArticleRepositoryPort for CREMESP source.
 * This is an outbound adapter in Hexagonal Architecture.
 *
 * Can be easily replaced with:
 * - PubMedArticleAdapter
 * - SciELOArticleAdapter
 * - CachedArticleAdapter
 */
@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class CremespArticleAdapter implements MedicalArticleRepositoryPort {

    private final HtmlFetchService htmlFetchService;

    @Value("${external-services.cremesp.base-url:https://cremesp.org.br/pesquisar.php}")
    private String baseUrl;

    @Value("${external-services.cremesp.max-content-length:8000}")
    private int maxContentLength;

    @Override
    public Optional<MedicalArticle> findByTopic(String topic) {
        if (topic == null || topic.isBlank()) {
            log.warn("Empty search topic");
            return Optional.empty();
        }

        log.info("Searching CREMESP - Topic: '{}'", topic);

        try {
            String searchUrl = buildSearchUrl(topic);
            log.info("Search URL: {}", searchUrl);

            String html = htmlFetchService.fetchHtml(searchUrl);

            if (html == null || html.isBlank()) {
                log.error("Empty HTML returned for URL: {}", searchUrl);
                return Optional.empty();
            }

            log.info("HTML received: {} characters", html.length());

            // Extract first article from search results
            String articleUrl = extractFirstArticleUrl(html);

            if (articleUrl == null) {
                log.warn("No specific article found, using search page content...");

                // Fallback: use search page content
                String searchPageContent = extractSearchPageContent(html);

                if (searchPageContent != null && searchPageContent.length() > 200) {
                    log.info("Using search page content: {} characters", searchPageContent.length());
                    log.info("📎 Source URL: {}", searchUrl);

                    String limitedContent = limitContent(searchPageContent);
                    return MedicalArticle.of(limitedContent, searchUrl);
                } else {
                    log.warn("Search page content too short or empty");
                    return Optional.empty();
                }
            }

            log.info("Article found: {}", articleUrl);
            log.info("📎 Source URL: {}", articleUrl);

            // Fetch complete article content
            String articleHtml = htmlFetchService.fetchHtml(articleUrl);

            if (articleHtml == null || articleHtml.isBlank()) {
                log.error("Failed to fetch article content: {}", articleUrl);
                return Optional.empty();
            }

            String content = extractArticleContent(articleHtml);

            if (content == null || content.length() < 100) {
                log.warn("Extracted content too short or empty");
                return Optional.empty();
            }

            // Limit content size for AI
            String limitedContent = limitContent(content);

            log.info("SUCCESS: Article processed - {} characters of content", limitedContent.length());
            return MedicalArticle.of(limitedContent, articleUrl);

        } catch (Exception e) {
            log.error("Error searching CREMESP: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private String buildSearchUrl(String topic) {
        return String.format("%s?hl=pt-BR&q=%s",
                baseUrl,
                topic.replace(" ", "+"));
    }

    private String extractSearchPageContent(String html) {
        try {
            Document doc = Jsoup.parse(html);
            doc.select("script, style, nav, header, footer, iframe, ads, .advertisement, form, input, button").remove();

            Elements snippets = doc.select(".gs_rs, .gs_a, .result-snippet, .search-result, p");

            if (!snippets.isEmpty()) {
                StringBuilder content = new StringBuilder();
                for (Element snippet : snippets) {
                    String text = snippet.text();
                    if (text.length() > 30) {
                        content.append(text).append(" ");
                    }
                }

                String result = content.toString().trim();
                log.debug("Content extracted from snippets: {} characters", result.length());
                return result;
            }

            String bodyText = doc.body().text();
            log.debug("Content extracted from body: {} characters", bodyText.length());
            return bodyText;

        } catch (Exception e) {
            log.error("Error extracting search page content: {}", e.getMessage());
            return null;
        }
    }

    private String extractFirstArticleUrl(String html) {
        try {
            Document doc = Jsoup.parse(html);
            Elements allLinks = doc.select("a[href]");
            log.info("Total links found: {}", allLinks.size());

            for (Element link : allLinks) {
                String href = link.attr("abs:href");
                String text = link.text();

                if (href == null || href.isBlank() ||
                    href.startsWith("#") ||
                    href.startsWith("javascript:") ||
                    !href.startsWith("http")) {
                    continue;
                }

                // Exclude non-content URLs
                if (href.contains("transparencia.") ||
                    href.contains("login") ||
                    href.contains("admin") ||
                    href.contains("pesquisar.php") ||
                    href.contains("busca") ||
                    href.contains("search")) {
                    log.debug("Skipping non-content URL: {}", href);
                    continue;
                }

                if (href.contains("cremesp.org.br") &&
                    (href.contains("/noticia/") ||
                     href.contains("/portal/") ||
                     href.contains("/noticias/") ||
                     href.contains("detalhe") ||
                     text.length() > 20)) {

                    log.info("Article found: {}", href);
                    return href;
                }
            }

            log.warn("No article link found in HTML");
            return null;

        } catch (Exception e) {
            log.error("Error extracting article URL: {}", e.getMessage());
            return null;
        }
    }

    private String extractArticleContent(String html) {
        try {
            Document doc = Jsoup.parse(html);
            doc.select("script, style, nav, header, footer, iframe, ads, .advertisement").remove();

            Element content = doc.selectFirst("article");

            if (content == null) {
                content = doc.selectFirst("main");
            }

            if (content == null) {
                content = doc.selectFirst("div.content, div.post-content, div.entry-content");
            }

            if (content == null) {
                content = doc.body();
            }

            if (content != null) {
                String text = content.text();
                log.debug("Content extracted: {} characters", text.length());
                return text;
            }

            log.warn("Could not extract content from HTML");
            return null;

        } catch (Exception e) {
            log.error("Error extracting article content: {}", e.getMessage());
            return null;
        }
    }

    private String limitContent(String content) {
        if (content.length() <= maxContentLength) {
            return content;
        }

        log.info("Limiting content from {} to {} characters",
                content.length(), maxContentLength);

        return content.substring(0, maxContentLength) + "...";
    }
}
