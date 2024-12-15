package com.example.conduit_springboot_vaadin.repository;

import com.example.conduit_springboot_vaadin.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

}
