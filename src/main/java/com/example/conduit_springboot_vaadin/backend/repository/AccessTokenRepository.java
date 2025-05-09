package com.example.conduit_springboot_vaadin.backend.repository;

import com.example.conduit_springboot_vaadin.backend.model.AccessToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccessTokenRepository extends MongoRepository<AccessToken, String> {
}
