package com.example.conduit_springboot_vaadin.service;

import com.example.conduit_springboot_vaadin.backend.common.util.JwtUtil;
import com.example.conduit_springboot_vaadin.backend.dto.user.*;
import com.example.conduit_springboot_vaadin.backend.exception.InvalidCredentialsException;
import com.example.conduit_springboot_vaadin.backend.exception.UserAlreadyExistsException;
import com.example.conduit_springboot_vaadin.backend.mapper.UserMapper;
import com.example.conduit_springboot_vaadin.backend.model.AccessToken;
import com.example.conduit_springboot_vaadin.backend.model.RefreshToken;
import com.example.conduit_springboot_vaadin.backend.model.User;
import com.example.conduit_springboot_vaadin.backend.repository.UserRepository;
import com.example.conduit_springboot_vaadin.backend.service.TokenService;
import com.example.conduit_springboot_vaadin.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;


    private RegisterUserDto registerUserDto;
    private User user;
    private UserDto userDto;
    private AccessToken accessTokenModel;
    private RefreshToken refreshTokenModel;
    String userId = "nonexistentUserId";

    @BeforeEach
    void setUp() {

        registerUserDto = RegisterUserDto.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("password123")
                .bio("A test user")
                .image("https://example.com/image.jpg")
                .following(List.of("userId1", "userId2"))
                .build();

        user = User.builder()
                .id("userId123")
                .username("testuser")
                .email("testuser@example.com")
                .password("hashedPassword")
                .bio("A test user")
                .image("https://example.com/image.jpg")
                .following(List.of("userId1", "userId2"))
                .build();

        userDto = UserDto.builder()
                .username("testuser")
                .email("testuser@example.com")
                .bio("A test user")
                .image("https://example.com/image.jpg")
                .following(List.of("userId1", "userId2"))
                .build();

        accessTokenModel = AccessToken.builder()
                .id("accessTokenId123")
                .userId(user.getId())
                .build();

        refreshTokenModel = RefreshToken.builder()
                .id("refreshTokenId123")
                .accessTokenId(accessTokenModel.getId())
                .build();

    }

    @Test
    void registerUser_Success() {

        when(userRepository.existsByEmail(registerUserDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerUserDto.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerUserDto.getPassword())).thenReturn("hashedPassword");
        when(userMapper.registerUserDtoToUser(registerUserDto)).thenReturn(new User());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        UserDto result = userService.registerUser(registerUserDto);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(registerUserDto.getUsername());
        assertThat(result.getEmail()).isEqualTo(registerUserDto.getEmail());
        assertThat(result.getBio()).isEqualTo(registerUserDto.getBio());
        assertThat(result.getImage()).isEqualTo(registerUserDto.getImage());
        assertThat(result.getFollowing()).isEqualTo(registerUserDto.getFollowing());

        verify(userRepository, times(1)).existsByEmail(registerUserDto.getEmail());
        verify(userRepository, times(1)).existsByUsername(registerUserDto.getUsername());
        verify(passwordEncoder, times(1)).encode(registerUserDto.getPassword());
        verify(userMapper, times(1)).registerUserDtoToUser(registerUserDto);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).userToUserDto(user);

    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {

        when(userRepository.existsByEmail(registerUserDto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(registerUserDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("email");

        verify(userRepository, times(1)).existsByEmail(registerUserDto.getEmail());
        verify(userRepository, never()).existsByUsername(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).registerUserDtoToUser((any()));
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).userToUserDto(any());

    }

    @Test
    void registerUser_UsernameAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail(registerUserDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerUserDto.getUsername())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(registerUserDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("username");

        // Verify interactions
        verify(userRepository, times(1)).existsByEmail(registerUserDto.getEmail());
        verify(userRepository, times(1)).existsByUsername(registerUserDto.getUsername());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).registerUserDtoToUser(any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).userToUserDto(any());
    }

    @Test
    void loginUser_Success() {

        LoginDto loginDto = LoginDto.builder()
                .email("testuser@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(true);

        when(tokenService.createAccessTokenModel(user.getId())).thenReturn(accessTokenModel);
        when(tokenService.createRefreshTokenModel(accessTokenModel.getId())).thenReturn(refreshTokenModel);

        when(jwtUtil.generateAccessToken(accessTokenModel.getId(), user.getId(), user.getEmail()))
                .thenReturn("access.jwt.token");
        when(jwtUtil.generateRefreshToken(refreshTokenModel.getId(), user.getId()))
                .thenReturn("refresh.jwt.token");

        AuthResponseDto result = userService.loginUser(loginDto);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access.jwt.token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh.jwt.token");
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getBio()).isEqualTo(user.getBio());
        assertThat(result.getImage()).isEqualTo(user.getImage());

        verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
        verify(passwordEncoder, times(1)).matches(loginDto.getPassword(), user.getPassword());
        verify(tokenService, times(1)).createAccessTokenModel(user.getId());
        verify(tokenService, times(1)).createRefreshTokenModel(accessTokenModel.getId());
        verify(jwtUtil, times(1)).generateAccessToken(accessTokenModel.getId(), user.getId(), user.getEmail());
        verify(jwtUtil, times(1)).generateRefreshToken(refreshTokenModel.getId(), user.getId());
    }

    @Test
    void loginUser_InvalidEmail_ThrowsException() {

        LoginDto loginDto = LoginDto.builder()
                .email("nonexistent@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loginUser(loginDto))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password.");

        verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(tokenService, never()).createAccessTokenModel(anyString());
        verify(tokenService, never()).createRefreshTokenModel(anyString());
        verify(jwtUtil, never()).generateAccessToken(anyString(), anyString(), anyString());
        verify(jwtUtil, never()).generateRefreshToken(anyString(), anyString());
    }

    @Test
    void loginUser_InvalidPassword_ThrowsException() {

        LoginDto loginDto = LoginDto.builder()
                .email("testuser@example.com")
                .password("wrongpassword")
                .build();

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userService.loginUser(loginDto))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password.");

        verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
        verify(passwordEncoder, times(1)).matches(loginDto.getPassword(), user.getPassword());
        verify(tokenService, never()).createAccessTokenModel(anyString());
        verify(tokenService, never()).createRefreshTokenModel(anyString());
        verify(jwtUtil, never()).generateAccessToken(anyString(), anyString(), anyString());
        verify(jwtUtil, never()).generateRefreshToken(anyString(), anyString());
    }

    @Test
    void loginUser_AccessTokenGenerationFails_ThrowsException() {

        // Arrange
        LoginDto loginDto = LoginDto.builder()
                .email("testuser@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(true);

        when(tokenService.createAccessTokenModel(user.getId())).thenReturn(accessTokenModel);
        when(tokenService.createRefreshTokenModel(accessTokenModel.getId())).thenReturn(refreshTokenModel);

        when(jwtUtil.generateAccessToken(accessTokenModel.getId(), user.getId(), user.getEmail()))
                .thenThrow(new RuntimeException("Token generation failed"));

        assertThatThrownBy(() -> userService.loginUser(loginDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Token generation failed");

        verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
        verify(passwordEncoder, times(1)).matches(loginDto.getPassword(), user.getPassword());
        verify(tokenService, times(1)).createAccessTokenModel(user.getId());
        verify(tokenService, times(1)).createRefreshTokenModel(accessTokenModel.getId());
        verify(jwtUtil, times(1)).generateAccessToken(accessTokenModel.getId(), user.getId(), user.getEmail());
        verify(jwtUtil, never()).generateRefreshToken(anyString(), anyString());
    }

    @Test
    void loginUser_RefreshTokenGenerationFails_ThrowsException() {

        LoginDto loginDto = LoginDto.builder()
                .email("testuser@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(true);

        when(tokenService.createAccessTokenModel(user.getId())).thenReturn(accessTokenModel);
        when(tokenService.createRefreshTokenModel(accessTokenModel.getId())).thenReturn(refreshTokenModel);

        when(jwtUtil.generateAccessToken(accessTokenModel.getId(), user.getId(), user.getEmail()))
                .thenReturn("access.jwt.token");
        when(jwtUtil.generateRefreshToken(refreshTokenModel.getId(), user.getId()))
                .thenThrow(new RuntimeException("Refresh token generation failed"));

        assertThatThrownBy(() -> userService.loginUser(loginDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Refresh token generation failed");

        verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
        verify(passwordEncoder, times(1)).matches(loginDto.getPassword(), user.getPassword());
        verify(tokenService, times(1)).createAccessTokenModel(user.getId());
        verify(tokenService, times(1)).createRefreshTokenModel(accessTokenModel.getId());
        verify(jwtUtil, times(1)).generateAccessToken(accessTokenModel.getId(), user.getId(), user.getEmail());
        verify(jwtUtil, times(1)).generateRefreshToken(refreshTokenModel.getId(), user.getId());
    }

    @Test
    void getCurrentUser_Success() {

        String userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getCurrentUser(userId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(userDto);

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).userToUserDto(user);
    }

    @Test
    void getCurrentUser_UserNotFound_ThrowsException() {

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser(userId))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with id: " + userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, never()).userToUserDto(any());

    }

    @Test
    void updateUser_Success_AllFieldsUpdated() {

        String userId = user.getId();

        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .username("updatedUser")
                .email("updateduser@example.com")
                .bio("Updated bio")
                .image("https://example.com/updated-image.jpg")
                .password("newpassword123")
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .username(updateUserDto.getUsername())
                .email(updateUserDto.getEmail())
                .bio(updateUserDto.getBio())
                .image(updateUserDto.getImage())
                .following(user.getFollowing()) // Assuming following list remains unchanged
                .password("hashedNewPassword")
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .username(updateUserDto.getUsername())
                .email(updateUserDto.getEmail())
                .bio(updateUserDto.getBio())
                .image(updateUserDto.getImage())
                .following(user.getFollowing())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(updateUserDto.getPassword())).thenReturn("hashedNewPassword");
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.userToUserDto(updatedUser)).thenReturn(updatedUserDto);

        UserDto result = userService.updateUser(updateUserDto, userId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(updatedUserDto);

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).encode(updateUserDto.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).userToUserDto(updatedUser);

    }

    @Test
    void updateUser_Success_SomeFieldsUpdated() {

        String userId = user.getId();

        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .email("partialupdate@example.com")
                .bio("Partially updated bio")
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .username(user.getUsername())
                .email(updateUserDto.getEmail())
                .bio(updateUserDto.getBio())
                .image(user.getImage())
                .following(user.getFollowing())
                .password(user.getPassword())
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .username(user.getUsername())
                .email(updateUserDto.getEmail())
                .bio(updateUserDto.getBio())
                .image(user.getImage())
                .following(user.getFollowing())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.userToUserDto(updatedUser)).thenReturn(updatedUserDto);

        UserDto result = userService.updateUser(updateUserDto, userId);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
        assertThat(result.getEmail()).isEqualTo(updateUserDto.getEmail());
        assertThat(result.getBio()).isEqualTo(updateUserDto.getBio());
        assertThat(result.getImage()).isEqualTo(user.getImage());
        assertThat(result.getFollowing()).isEqualTo(user.getFollowing());

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).userToUserDto(updatedUser);
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {

        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .username("newusername")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(updateUserDto, userId))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with ID: " + userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, never()).userToUserDto(any());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

}
