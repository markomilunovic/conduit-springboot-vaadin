package com.example.conduit_springboot_vaadin.backend.mapper;

import com.example.conduit_springboot_vaadin.backend.dto.article.AuthorDto;
import com.example.conduit_springboot_vaadin.backend.model.User;
import com.example.conduit_springboot_vaadin.backend.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    private final UserRepository userRepository;

    public AuthorMapper (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthorDto userToAuthorDto(String authorUsername, String currentUserId) {

        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        boolean isFollowing = currentUserId != null && author.getFollowing().contains(currentUserId);

        return AuthorDto.builder()
                .username(author.getUsername())
                .bio(author.getBio())
                .image(author.getImage())
                .following(isFollowing)
                .build();
    }

}
