package com.example.conduit_springboot_vaadin.service;

import com.example.conduit_springboot_vaadin.dto.profile.ProfileDto;
import com.example.conduit_springboot_vaadin.mapper.ProfileMapper;
import com.example.conduit_springboot_vaadin.model.User;
import com.example.conduit_springboot_vaadin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    @Autowired
    public ProfileService(
            UserRepository userRepository,
            ProfileMapper profileMapper
    ) {
        this.userRepository = userRepository;
        this.profileMapper = profileMapper;
    }

    /**
     * Retrieves the profile of a user by username.
     *
     * @param username      The username of the profile to retrieve.
     * @param currentUserId The ID of the currently authenticated user (can be null).
     * @return The ProfileDto containing profile information.
     * @throws UsernameNotFoundException if the user with the given username does not exist.
     */
    public ProfileDto getProfile(String username, String currentUserId) {
        log.info("Fetching profile for username: {}", username);

        User targetUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        boolean isFollowing = false;
        if (currentUserId != null) {
            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));
            isFollowing = currentUser.getFollowing() != null && currentUser.getFollowing().contains(targetUser.getId());
        }

        log.debug("Profile fetched successfully for username: {}", username);

        return profileMapper.userToProfileDto(targetUser, isFollowing);
    }
}
