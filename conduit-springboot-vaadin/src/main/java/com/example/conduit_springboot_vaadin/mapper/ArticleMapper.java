package com.example.conduit_springboot_vaadin.mapper;

import com.example.conduit_springboot_vaadin.dto.article.ArticleDto;
import com.example.conduit_springboot_vaadin.dto.article.ArticleListDto;
import com.example.conduit_springboot_vaadin.dto.article.CreateArticleDto;
import com.example.conduit_springboot_vaadin.model.Article;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class ArticleMapper {

    private final AuthorMapper authorMapper;

    public ArticleMapper(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    public Article createArticleDtoToArticle (CreateArticleDto createArticleDto, String authorUsername, String slug) {

        return Article.builder()
                .title(createArticleDto.getTitle())
                .description(createArticleDto.getDescription())
                .body(createArticleDto.getBody())
                .tagList(createArticleDto.getTagList() != null ? createArticleDto.getTagList() : List.of())
                .slug(slug)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .author(authorUsername)
                .favoritedBy(List.of())
                .build();

    }

    public ArticleDto articleToArticleDto(Article article, String currentUserId) {

        boolean isFavorited = currentUserId != null && article.getFavoritedBy().contains(currentUserId);

        return ArticleDto.builder()
                .slug(article.getSlug())
                .title(article.getTitle())
                .description(article.getDescription())
                .body(article.getBody())
                .tagList(article.getTagList())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .favorited(isFavorited)
                .favoritesCount(article.getFavoritedBy().size())
                .author(authorMapper.userToAuthorDto(article.getAuthor(), currentUserId))
                .build();
    }

    public ArticleListDto articleToArticleListDto(Article article, String currentUserId) {

        boolean isFavorited = currentUserId != null && article.getFavoritedBy().contains(currentUserId);

        return ArticleListDto.builder()
                .slug(article.getSlug())
                .title(article.getTitle())
                .description(article.getDescription())
                .tagList(article.getTagList())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .favorited(isFavorited)
                .favoritesCount(article.getFavoritedBy().size())
                .author(authorMapper.userToAuthorDto(article.getAuthor(), currentUserId))
                .build();
    }
}
