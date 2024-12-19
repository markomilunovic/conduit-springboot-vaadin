package com.example.conduit_springboot_vaadin.service;

import com.example.conduit_springboot_vaadin.dto.profile.ProfileDto;
import com.example.conduit_springboot_vaadin.mapper.ProfileMapper;
import com.example.conduit_springboot_vaadin.model.User;
import com.example.conduit_springboot_vaadin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@Service
@Transactional
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

    /**
     * Authenticated user follows the target user by username.
     *
     * @param targetUsername The username of the user to follow.
     * @param currentUserId  The ID of the currently authenticated user.
     * @return The updated ProfileDto of the target user with following status.
     * @throws UsernameNotFoundException if the target user does not exist.
     * @throws IllegalArgumentException  if the current user tries to follow themselves or already follows the target user.
     */
    public ProfileDto followUser(String targetUsername, String currentUserId) {

        log.info("User with ID: {} is attempting to follow user: {}", currentUserId, targetUsername);

        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + targetUsername));

        if (targetUser.getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Users cannot follow themselves.");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));

        if (currentUser.getFollowing().contains(targetUser.getId())) {
            throw new IllegalArgumentException("You are already following this user.");
        }

        currentUser.getFollowing().add(targetUser.getId());
        userRepository.save(currentUser);

        log.info("User with ID: {} successfully followed user: {}", currentUserId, targetUsername);

        return profileMapper.userToProfileDto(targetUser, true);

    }

    /**
     * Authenticated user unfollows the target user by username.
     *
     * @param targetUsername The username of the user to unfollow.
     * @param currentUserId  The ID of the currently authenticated user.
     * @return The updated ProfileDto of the target user with following status.
     * @throws UsernameNotFoundException if the target user does not exist.
     * @throws IllegalArgumentException  if the current user tries to unfollow themselves or does not follow the target user.
     */
    public ProfileDto unfollowUser(String targetUsername, String currentUserId) {

        log.info("User with ID: {} is attempting to unfollow user: {}", currentUserId, targetUsername);

        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + targetUsername));

        if (targetUser.getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Users cannot unfollow themselves.");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));

        if (!currentUser.getFollowing().contains(targetUser.getId())) {
            throw new IllegalArgumentException("You are not following this user.");
        }

        currentUser.getFollowing().remove(targetUser.getId());
        userRepository.save(currentUser);

        log.info("User with ID: {} successfully unfollowed user: {}", currentUserId, targetUsername);

        return profileMapper.userToProfileDto(targetUser, false);
    }

}
