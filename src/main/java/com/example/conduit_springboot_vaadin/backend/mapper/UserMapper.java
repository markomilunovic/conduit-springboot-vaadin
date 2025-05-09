package com.example.conduit_springboot_vaadin.backend.mapper;

import com.example.conduit_springboot_vaadin.backend.dto.user.RegisterUserDto;
import com.example.conduit_springboot_vaadin.backend.dto.user.UserDto;
import com.example.conduit_springboot_vaadin.backend.model.User;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {

    public User registerUserDtoToUser(RegisterUserDto registerUserDto) {
        return User.builder()
                .username(registerUserDto.getUsername())
                .email(registerUserDto.getEmail())
                .password(registerUserDto.getPassword())
                .bio(registerUserDto.getBio())
                .image(registerUserDto.getImage())
                .build();
    }

    public UserDto userToUserDto(User user) {
        return UserDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .image(user.getImage())
                .following(user.getFollowing())
                .build();
    }

}
