package com.example.conduit_springboot_vaadin.mapper;

import com.example.conduit_springboot_vaadin.dto.profile.ProfileDto;
import com.example.conduit_springboot_vaadin.model.User;
import org.springframework.stereotype.Component;


@Component
public class ProfileMapper {

    public ProfileDto userToProfileDto(User user, boolean isFollowing) {
        return ProfileDto.builder()
                .username(user.getUsername())
                .bio(user.getBio())
                .image(user.getImage())
                .following(isFollowing)
                .build();
    }
}