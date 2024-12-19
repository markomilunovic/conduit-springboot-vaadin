package com.example.conduit_springboot_vaadin.controller;

import com.example.conduit_springboot_vaadin.dto.user.*;
import com.example.conduit_springboot_vaadin.exception.InvalidCredentialsException;
import com.example.conduit_springboot_vaadin.security.CustomUserDetails;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
                    content = @Content(schema = @Schema(implementation = InvalidCredentialsException.class))),
            @ApiResponse(responseCode = "422", description = "Validation failed due to invalid input parameters",
                    content = @Content(schema = @Schema(implementation = MethodArgumentNotValidException.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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

    @Operation(
            summary = "Update user by email",
            description = "Updates the user by its email."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = UsernameNotFoundException.class))),
            @ApiResponse(responseCode = "422", description = "Validation failed due to invalid input parameters",
                    content = @Content(schema = @Schema(implementation = MethodArgumentNotValidException.class)))
    })
    @PutMapping("/user")
    public ResponseEntity<ResponseDto<UserDto>> updateUser(
            @Valid @RequestBody UpdateUserDto userDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Request received to update user with ID: {}", userDetails.getId());
        log.debug("Received request to update user with data: {}", userDto);

        UserDto updatedUser = userService.updateUser(userDto, userDetails.getId());

        log.info("User updated successfully: {}", updatedUser.getEmail());
        log.debug("UserDto updated with data: {}", updatedUser);

        ResponseDto<UserDto> response = new ResponseDto<>(updatedUser, "User updated successfully");
        return ResponseEntity.ok(response);

    }

}
