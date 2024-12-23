package com.example.conduit_springboot_vaadin.repository;

import com.example.conduit_springboot_vaadin.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> findByArticle(String articleSlug);

}
