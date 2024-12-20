package com.example.conduit_springboot_vaadin.repository;

import com.example.conduit_springboot_vaadin.model.Article;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ArticleRepository extends MongoRepository<Article, String> {
    Optional<Article> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
