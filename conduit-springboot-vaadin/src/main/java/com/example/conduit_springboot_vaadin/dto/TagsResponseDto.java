package com.example.conduit_springboot_vaadin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for responding with a list of tags.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagsResponseDto {
    private List<String> tags;
}

