package com.example.conduit_springboot_vaadin.backend.repository;

import com.example.conduit_springboot_vaadin.backend.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
}
