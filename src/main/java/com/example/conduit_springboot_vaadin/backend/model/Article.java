package com.example.conduit_springboot_vaadin.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an article within the Conduit application.
 * <p>
 * Each article document is stored in the "articles" collection of MongoDB.
 * Articles contain metadata, content (body), tags, and information about the author
 * and users who have favorited the article.
 * </p>
 */
@Document(collection = "articles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    @Id
    private String id;

    @Indexed(unique=true)
    private String slug;
    private String title;
    private String description;
    private String body;
    private List<String> tagList = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
    private String author;
    private List<String> favoritedBy = new ArrayList<>();

}

