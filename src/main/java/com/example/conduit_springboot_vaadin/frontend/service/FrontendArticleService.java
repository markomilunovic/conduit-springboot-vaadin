package com.example.conduit_springboot_vaadin.frontend.service;

import com.example.conduit_springboot_vaadin.backend.dto.article.ArticleListDto;
import com.example.conduit_springboot_vaadin.backend.dto.article.ArticleListResponseDto;
import com.example.conduit_springboot_vaadin.backend.dto.user.ResponseDto;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FrontendArticleService {

    private final RestTemplate restTemplate;

    public FrontendArticleService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Map<String, Object>> getArticles(String tag, String author, String favorited, int limit, int offset) {
        try {
            String url = "http://localhost:8080/api/articles?limit=" + limit + "&offset=" + offset;
            if (tag != null) url += "&tag=" + tag;
            if (author != null) url += "&author=" + author;
            if (favorited != null) url += "&favorited=" + favorited;

            ResponseEntity<ResponseDto<ArticleListResponseDto>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
            );

            if (response.getBody() != null && response.getBody().getData() != null) {
                List<ArticleListDto> articles = response.getBody().getData().getArticles();

                return articles.stream()
                        .map(article -> Map.of(
                                "title", article.getTitle(),
                                "author", article.getAuthor(),
                                "description", article.getDescription()
                        ))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            Notification.show("Failed to load articles: " + e.getMessage());
        }
        return Collections.emptyList();
    }
}
