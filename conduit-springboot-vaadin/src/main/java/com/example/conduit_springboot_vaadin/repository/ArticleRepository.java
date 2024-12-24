package com.example.conduit_springboot_vaadin.repository;

import com.example.conduit_springboot_vaadin.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends MongoRepository<Article, String> {
    Optional<Article> findBySlug(String slug);
    boolean existsBySlug(String slug);

    @NonNull Page<Article> findAll(@NonNull Pageable page);

    Page<Article> findByAuthor(String author, Pageable page);

    Page<Article> findByTagList(String tag, Pageable page);

    Page<Article> findByFavoritedBy(String userId, Pageable page);

    Page<Article> findByAuthorAndTagList(String author, String tag, Pageable page);

    Page<Article> findByAuthorAndFavoritedBy(String author, String userId, Pageable page);

    Page<Article> findByTagListAndFavoritedBy(String tag, String userId, Pageable page);

    Page<Article> findByAuthorAndTagListAndFavoritedBy(String author, String tag, String userId, Pageable page);

    Page<Article> findByAuthorIn(List<String> authors, Pageable pageable);
}
