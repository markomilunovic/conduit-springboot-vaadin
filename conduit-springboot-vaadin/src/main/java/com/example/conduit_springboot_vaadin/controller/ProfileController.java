package com.example.conduit_springboot_vaadin.controller;

import com.example.conduit_springboot_vaadin.dto.profile.ProfileDto;
import com.example.conduit_springboot_vaadin.dto.user.ResponseDto;
import com.example.conduit_springboot_vaadin.exception.InvalidCredentialsException;
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

    @Operation(
            summary = "Follow User",
            description = "Authenticated user follows another user by username."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully followed the user",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request (e.g., already following, trying to follow self)",
                    content = @Content(schema = @Schema(implementation = IllegalArgumentException.class))),
            @ApiResponse(responseCode = "404", description = "Target user not found",
                    content = @Content(schema = @Schema(implementation = UsernameNotFoundException.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = InvalidCredentialsException.class)))
    })
    @PostMapping("/{username}/follow")
    public ResponseEntity<ResponseDto<ProfileDto>> followUser(
            @PathVariable String username,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ProfileDto updatedProfile = profileService.followUser(username, userDetails.getId());
        ResponseDto<ProfileDto> response = new ResponseDto<>(updatedProfile, "Successfully followed the user.");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Unfollow User",
            description = "Authenticated user unfollows another user by username."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully unfollowed the user",
                    content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request (e.g., not following, trying to unfollow self)",
                    content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = IllegalArgumentException.class))),
            @ApiResponse(responseCode = "404", description = "Target user not found",
                    content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsernameNotFoundException.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = InvalidCredentialsException.class)))
    })
    @DeleteMapping("/{username}/follow")
    public ResponseEntity<ResponseDto<ProfileDto>> unfollowUser(
            @PathVariable String username,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ProfileDto updatedProfile = profileService.unfollowUser(username, userDetails.getId());
        ResponseDto<ProfileDto> response = new ResponseDto<>(updatedProfile, "Successfully unfollowed the user.");
        return ResponseEntity.ok(response);
    }
}
