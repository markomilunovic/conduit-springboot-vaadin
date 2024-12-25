package com.example.conduit_springboot_vaadin.backend.service;

import com.example.conduit_springboot_vaadin.backend.dto.TagsResponseDto;
import com.example.conduit_springboot_vaadin.backend.model.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class TagService {

    private final MongoTemplate mongoTemplate;

    public TagService(
            MongoTemplate mongoTemplate
    ) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Retrieves all unique tags used across all articles.
     * <p>
     * This method fetches distinct tags from the repository and returns them
     * encapsulated in a {@link TagsResponseDto}.
     * </p>
     *
     * @return A {@link TagsResponseDto} containing the list of unique tags.
     */
    public TagsResponseDto getAllTags() {

        log.info("Retrieving all distinct tags from articles.");

        List<String> tags = mongoTemplate.findDistinct(
                new Query(),
                "tagList",
                Article.class,
                String.class
        );

        log.info("Retrieved {} distinct tags.", tags.size());

        return TagsResponseDto.builder()
                .tags(tags)
                .build();
    }

}
