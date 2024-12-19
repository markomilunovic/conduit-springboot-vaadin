package com.example.conduit_springboot_vaadin.service;

import com.example.conduit_springboot_vaadin.common.util.JwtUtil;
import com.example.conduit_springboot_vaadin.dto.user.*;
import com.example.conduit_springboot_vaadin.exception.InvalidCredentialsException;
import com.example.conduit_springboot_vaadin.exception.UserAlreadyExistsException;
import com.example.conduit_springboot_vaadin.mapper.UserMapper;
import com.example.conduit_springboot_vaadin.model.AccessToken;
import com.example.conduit_springboot_vaadin.model.RefreshToken;
import com.example.conduit_springboot_vaadin.model.User;
import com.example.conduit_springboot_vaadin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Autowired
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            JwtUtil jwtUtil,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    /**
     * Registers a new user in the system.
     * <p>
     * This method checks if a user with the provided email already exists.
     * If so, it throws a {@link UserAlreadyExistsException}. Otherwise,
     * it hashes the user's password, converts the {@link RegisterUserDto}
     * to a {@link User} entity, saves it in the repository, and returns
     * the saved user as a {@link UserDto}.
     * </p>
     *
     * @param registerUserDto The data transfer object containing user registration information.
     * @return The newly registered user as a {@link UserDto}.
     * @throws UserAlreadyExistsException if a user with the same email already exists.
     */
    public UserDto registerUser(RegisterUserDto registerUserDto) {

        log.info("Registering user with email: {}", registerUserDto.getEmail());

        if (userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new UserAlreadyExistsException("email", registerUserDto.getEmail());
        }

        if (userRepository.existsByUsername(registerUserDto.getUsername())) {
            throw new UserAlreadyExistsException("username", registerUserDto.getUsername());
        }

        String hashedPassword = passwordEncoder.encode(registerUserDto.getPassword());
        log.debug("Password encoded for user registration");

        User user = userMapper.registerUserDtoToUser(registerUserDto);
        user.setPassword(hashedPassword);
        user = userRepository.save(user);

        log.info("User saved with ID: {}", user.getId());

        return userMapper.userToUserDto(user);

    }

    /**
     * Authenticates a user based on provided login credentials and generates authentication tokens.
     * <p>
     * This method verifies the user's username and password. If valid, it generates a new access token
     * and a refresh token for the user. The generated tokens are stored in the database for later validation.
     * </p>
     *
     * @param loginDto The data transfer object containing user login information, such as username and password.
     * @return A {@link AuthResponseDto} containing the access token, refresh token, and user ID.
     * @throws InvalidCredentialsException if the provided username or password is incorrect.
     */
    public AuthResponseDto loginUser(LoginDto loginDto) {

        log.info("Logging in user with email: {}", loginDto.getEmail());

        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        log.debug("User found with ID: {}", user.getId());

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        log.info("Login successful for user ID: {}", user.getId());
        log.debug("Generating access and refresh tokens for user ID: {}", user.getId());

        AccessToken accessTokenModel = tokenService.createAccessTokenModel(user.getId());
        RefreshToken refreshTokenModel = tokenService.createRefreshTokenModel(accessTokenModel.getId());

        log.debug("Access token entity created with ID: {}", accessTokenModel.getId());
        log.debug("Refresh token entity created with ID: {}", refreshTokenModel.getId());

        String accessToken = jwtUtil.generateAccessToken(
                accessTokenModel.getId(),
                user.getId(),
                user.getEmail()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                refreshTokenModel.getId(),
                user.getId()
        );

        log.debug("Access token generated: {}", accessToken);
        log.debug("Refresh token generated: {}", refreshToken);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .bio(user.getBio())
                .image(user.getImage())
                .build();
    }

    /**
     * Retrieves the current user by their ID.
     *
     * @param userId The ID of the user.
     * @return The {@link UserDto} representing the user.
     * @throws UsernameNotFoundException if the user is not found.
     */
    public UserDto getCurrentUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        return userMapper.userToUserDto(user);
    }

    /**
     * Updates the authenticated user's information based on the provided data.
     * <p>
     * This method retrieves the user from the repository using their ID, updates the user's username, bio, image, and password
     * if they are provided in {@link UpdateUserDto}, encodes the new password using {@link PasswordEncoder} before setting it,
     * saves the updated user back to the repository, and maps the updated user entity to a {@link UserDto} for response.
     * </p>
     *
     * @param updateUserDto The data transfer object containing the user's updated information.
     *                      It may include fields such as {@code username}, {@code email}, {@code bio}, {@code image}, and {@code password}.
     * @param userId        The ID of the user to update.
     * @return A {@link UserDto} representing the updated user information.
     * @throws UsernameNotFoundException if no user is found with the provided ID.
     * @throws IllegalArgumentException  if {@link UpdateUserDto} contains invalid data.
     */
    public UserDto updateUser(UpdateUserDto updateUserDto, String userId) {
        log.info("Logging user with ID: {}", userId);
        log.debug("Logging updateUserDto data: {}", updateUserDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        if (updateUserDto.getUsername() != null) {
            user.setUsername(updateUserDto.getUsername());
        }

        if (updateUserDto.getEmail() != null) {
            user.setEmail(updateUserDto.getEmail());
        }

        if (updateUserDto.getBio() != null) {
            user.setBio(updateUserDto.getBio());
        }

        if (updateUserDto.getImage() != null) {
            user.setImage(updateUserDto.getImage());
        }

        if (updateUserDto.getPassword() != null) {
            String hashedPassword = passwordEncoder.encode(updateUserDto.getPassword());
            user.setPassword(hashedPassword);
            log.debug("Password encoded and set for user update");
        }

        user = userRepository.save(user);
        log.info("User updated successfully: {}", user.getId());

        UserDto updatedUserDto = userMapper.userToUserDto(user);
        log.debug("Converted updated User to UserDto: {}", updatedUserDto);

        return updatedUserDto;

    }

}
