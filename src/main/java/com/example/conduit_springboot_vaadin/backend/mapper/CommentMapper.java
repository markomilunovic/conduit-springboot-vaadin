package com.example.conduit_springboot_vaadin.backend.mapper;

import com.example.conduit_springboot_vaadin.backend.dto.comment.AddCommentDto;
import com.example.conduit_springboot_vaadin.backend.dto.comment.CommentDto;
import com.example.conduit_springboot_vaadin.backend.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CommentMapper {

    private final AuthorMapper authorMapper;

    @Autowired
    public CommentMapper(
            AuthorMapper authorMapper
    ) {
        this.authorMapper = authorMapper;
    }

    public Comment addCommentDtoToComment(AddCommentDto addCommentDto, String authorUsername, String slug) {

        return Comment.builder()
                .body(addCommentDto.getBody())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .article(slug)
                .author(authorUsername)
                .build();
    }

    public CommentDto commentToCommentDto(Comment comment, String currentUserId, String articleAuthor) {

        return CommentDto.builder()
                .id(comment.getId())
                .body(comment.getBody())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .author(authorMapper.userToAuthorDto(articleAuthor, currentUserId))
                .build();
    }

}
