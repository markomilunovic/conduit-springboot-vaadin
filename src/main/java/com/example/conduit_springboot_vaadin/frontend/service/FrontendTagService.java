package com.example.conduit_springboot_vaadin.frontend.service;

import com.example.conduit_springboot_vaadin.backend.dto.TagsResponseDto;
import com.example.conduit_springboot_vaadin.backend.dto.user.ResponseDto;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class FrontendTagService {

    private final RestTemplate restTemplate;

    public FrontendTagService(
            RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    public List<String> getTags() {
        try {
            String url = "http://localhost:8080/api/tags";

            ResponseEntity<ResponseDto<TagsResponseDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData().getTags();
            }
        } catch (Exception e) {
            Notification.show("Failed to load tags: " + e.getMessage());
        }

        return Collections.emptyList();
    }

}
