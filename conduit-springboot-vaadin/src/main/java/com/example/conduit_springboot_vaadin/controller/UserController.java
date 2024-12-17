package com.example.conduit_springboot_vaadin.controller;

import com.example.conduit_springboot_vaadin.dto.*;
import com.example.conduit_springboot_vaadin.security.CustomUserDetails;
import com.example.conduit_springboot_vaadin.security.CustomUserDetailsService;
import com.example.conduit_springboot_vaadin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    /**
     * Registers a new user.
     *
     * @param registerUserDto The data transfer object containing user registration information.
     * @return A {@link ResponseEntity} containing a {@link ResponseDto} with the registered {@link UserDto}
     *         and a success message.
     */
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user and returns the user details along with a success message."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation failed due to invalid input parameters",
                    content = @Content(schema = @Schema(implementation = MethodArgumentNotValidException.class)))
    })
    @PostMapping
    public ResponseEntity<ResponseDto<UserDto>> register(@Valid @RequestBody RegisterUserDto registerUserDto) {

        log.info("Registering a new user with email: {}", registerUserDto.getEmail());
        log.debug("Register request data: {}", registerUserDto);

        UserDto userDto = userService.registerUser(registerUserDto);

        log.info("User registered successfully: {}", userDto.getEmail());

        ResponseDto<UserDto> response = new ResponseDto<>(userDto, "User registered successfully.");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Authenticate user and return tokens",
            description = "Logs in the user and returns access and refresh tokens along with user ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Invalid login data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation failed due to invalid input parameters",
                    content = @Content(schema = @Schema(implementation = MethodArgumentNotValidException.class)))
    })
    @PostMapping("/users/login")
    public ResponseEntity<ResponseDto<AuthResponseDto>> login(@Valid @RequestBody LoginDto loginDto) {

        log.info("User login attempt with email: {}", loginDto.getEmail());
        log.debug("Login request data: {}", loginDto);

        AuthResponseDto authResponseDto = userService.loginUser(loginDto);

        log.info("Login successful for user: {}", loginDto.getEmail());

        ResponseDto<AuthResponseDto> response = new ResponseDto<>(authResponseDto, "Login successful");
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the current authenticated user's information.
     *
     * @param userDetails The {@link CustomUserDetails} of the authenticated user.
     * @return A {@link ResponseEntity} containing the {@link UserDto} of the current user.
     */
    @Operation(
            summary = "Get Current User",
            description = "Retrieves the current authenticated user's information."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/user")
    public ResponseEntity<ResponseDto<UserDto>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserDto userDto = userService.getCurrentUser(userDetails.getId());
        ResponseDto<UserDto> response = new ResponseDto<>(userDto, "User retrieved successfully.");
        return ResponseEntity.ok(response);
    }

}
