package com.example.conduit_springboot_vaadin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents a refresh token associated with an access token, used for generating
 * new access tokens. Contains information about the token's status, expiration, and timestamps.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_tokens")
public class RefreshToken {

    @Id
    private String id;
    private String accessTokenId;
    private Boolean isRevoked = false;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt = LocalDateTime.now();

}

