package com.example.conduit_springboot_vaadin.controller;

import com.example.conduit_springboot_vaadin.dto.profile.ProfileDto;
import com.example.conduit_springboot_vaadin.dto.user.ResponseDto;
import com.example.conduit_springboot_vaadin.security.CustomUserDetails;
import com.example.conduit_springboot_vaadin.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }


    @Operation(
            summary = "Get Profile",
            description = "Retrieves a user's profile by username."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = UsernameNotFoundException.class)))
    })
    @GetMapping("/{username}")
    public ResponseEntity<ResponseDto<ProfileDto>> getProfile(
            @PathVariable String username,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        log.info("Request received to fetch profile for username: {}", username);

        String currentUserId = (userDetails != null) ? userDetails.getId() : null;
        ProfileDto profile = profileService.getProfile(username, currentUserId);
        ResponseDto<ProfileDto> response = new ResponseDto<>(profile, "Profile retrieved successfully.");
        return ResponseEntity.ok(response);
    }
}
