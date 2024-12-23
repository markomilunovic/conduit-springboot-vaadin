package com.example.conduit_springboot_vaadin.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListCommentDto {

    @Schema(description = "List of all the comments for the given article.")
    private List<CommentDto> comments;

}
