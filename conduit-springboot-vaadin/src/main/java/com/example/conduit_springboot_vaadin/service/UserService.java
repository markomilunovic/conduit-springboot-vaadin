package com.example.conduit_springboot_vaadin.service;

import com.example.conduit_springboot_vaadin.dto.RegisterUserDto;
import com.example.conduit_springboot_vaadin.dto.UserDto;
import com.example.conduit_springboot_vaadin.exception.UserAlreadyExistsException;
import com.example.conduit_springboot_vaadin.mapper.UserMapper;
import com.example.conduit_springboot_vaadin.model.User;
import com.example.conduit_springboot_vaadin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
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

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
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
            throw new UserAlreadyExistsException(registerUserDto.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(registerUserDto.getPassword());
        log.debug("Password encoded for user registration");

        User user = userMapper.registerUserDtoToUser(registerUserDto);
        user.setPassword(hashedPassword);
        user = userRepository.save(user);

        log.info("User saved with ID: {}", user.getId());

        return userMapper.userToUserDto(user);

    }

}
