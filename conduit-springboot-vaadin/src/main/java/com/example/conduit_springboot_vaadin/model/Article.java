package com.example.conduit_springboot_vaadin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
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
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    @Id
    private String id;
    private String slug;
    private String title;
    private String description;
    private String body;
    private List<String> tagList;
    private Instant createdAt;
    private Instant updatedAt;
    private String author;
    private List<String> favoritedBy;

}

