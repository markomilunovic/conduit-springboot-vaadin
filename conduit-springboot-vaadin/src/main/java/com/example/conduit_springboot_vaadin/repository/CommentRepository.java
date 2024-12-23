package com.example.conduit_springboot_vaadin.repository;

import com.example.conduit_springboot_vaadin.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, String> {
}
