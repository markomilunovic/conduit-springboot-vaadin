package com.example.conduit_springboot_vaadin.repository;

import com.example.conduit_springboot_vaadin.model.AccessToken;
import com.example.conduit_springboot_vaadin.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccessTokenRepository extends MongoRepository<AccessToken, String> {
}
