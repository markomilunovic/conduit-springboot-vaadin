package com.example.conduit_springboot_vaadin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/**
 * Represents a comment on an article within the Conduit application.
 * <p>
 * Each comment document is stored in the "comments" collection of MongoDB.
 * Comments reference the article they belong to and the user who created them.
 * </p>
 */
@Document(collection = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    private String id;
    private String body;
    private Instant createdAt;
    private Instant updatedAt;
    private String article;
    private String author;

}
