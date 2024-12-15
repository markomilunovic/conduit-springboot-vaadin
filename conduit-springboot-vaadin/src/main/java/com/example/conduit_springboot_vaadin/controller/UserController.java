package com.example.conduit_springboot_vaadin.controller;

import com.example.conduit_springboot_vaadin.dto.RegisterUserDto;
import com.example.conduit_springboot_vaadin.dto.ResponseDto;
import com.example.conduit_springboot_vaadin.dto.UserDto;
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
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/user")
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
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<ResponseDto<UserDto>> register(@Valid @RequestBody RegisterUserDto registerUserDto) {

        log.info("Registering a new user with email: {}", registerUserDto.getEmail());
        log.debug("Register request data: {}", registerUserDto);

        UserDto userDto = userService.registerUser(registerUserDto);

        log.info("User registered successfully: {}", userDto.getEmail());

        ResponseDto<UserDto> response = new ResponseDto<>(userDto, "User registered successfully.");
        return ResponseEntity.ok(response);
    }

}
