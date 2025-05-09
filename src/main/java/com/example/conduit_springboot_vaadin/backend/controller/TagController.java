package com.example.conduit_springboot_vaadin.backend.controller;

import com.example.conduit_springboot_vaadin.backend.dto.TagsResponseDto;
import com.example.conduit_springboot_vaadin.backend.dto.user.ResponseDto;
import com.example.conduit_springboot_vaadin.backend.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(
            TagService tagService
    ) {
        this.tagService = tagService;
    }

    @Operation(
            summary = "Get all tags",
            description = "Retrieves a list of all unique tags used across all articles."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tags retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))
            )
    })
    @GetMapping
    public ResponseEntity<ResponseDto<TagsResponseDto>> getTags() {
        log.info("Request received to retrieve all tags.");

        TagsResponseDto tagsResponseDto = tagService.getAllTags();

        ResponseDto<TagsResponseDto> response = new ResponseDto<>(tagsResponseDto, "Tags retrieved successfully.");

        log.info("Returning {} tags.", tagsResponseDto.getTags().size());

        return ResponseEntity.ok(response);
    }

}
