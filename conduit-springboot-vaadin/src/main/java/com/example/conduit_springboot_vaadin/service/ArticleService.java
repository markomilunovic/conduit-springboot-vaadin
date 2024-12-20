package com.example.conduit_springboot_vaadin.service;

import com.example.conduit_springboot_vaadin.dto.article.ArticleDto;
import com.example.conduit_springboot_vaadin.dto.article.CreateArticleDto;
import com.example.conduit_springboot_vaadin.exception.ArticleNotFoundException;
import com.example.conduit_springboot_vaadin.mapper.ArticleMapper;
import com.example.conduit_springboot_vaadin.model.Article;
import com.example.conduit_springboot_vaadin.repository.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional
public class ArticleService{

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    public ArticleService (
            ArticleRepository articleRepository,
            ArticleMapper articleMapper
    ) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
    }

    public ArticleDto createArticle(
            CreateArticleDto createArticleDto,
            String authorUsername,
            String currentUserId
    ) {

        String slug = generateUniqueSlug(createArticleDto.getTitle());

        Article article = articleMapper.createArticleDtoToArticle(createArticleDto, authorUsername, slug);

        Article savedArticle = articleRepository.save(article);

        return articleMapper.articleToArticleDto(savedArticle, currentUserId);
    }

    public ArticleDto getArticleBySlug(String slug) {

        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ArticleNotFoundException(slug));

        return articleMapper.articleToArticleDto(article, null);
    }

    /**
     * Generates a unique slug for the article based on its title.
     *
     * @param title The title of the article.
     * @return A unique slug string.
     */
    private String generateUniqueSlug(String title) {

        String baseSlug = title.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        String slug = baseSlug;
        int count = 1;

        while (articleRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + count++;
        }

        return slug;
    }
}
