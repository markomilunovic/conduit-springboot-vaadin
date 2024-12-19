package com.example.conduit_springboot_vaadin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents an access token for user authentication. This document stores information about
 * the token's status, expiration, and timestamps for creation and updates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "access_tokens")
public class AccessToken {

    @Id
    private String id;
    private String userId;
    private Boolean isRevoked = false;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt = LocalDateTime.now();


}
