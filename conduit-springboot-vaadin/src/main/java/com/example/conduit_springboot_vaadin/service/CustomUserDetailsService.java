package com.example.conduit_springboot_vaadin.service;


import com.example.conduit_springboot_vaadin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for loading user-specific data for authentication and authorization purposes.
 * <p>
 * This class implements {@link UserDetailsService}, providing methods to retrieve user details
 * based on either username or user ID. It is used by Spring Security during authentication to load
 * user data from the database.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by username for authentication.
     * <p>
     * This method retrieves user details based on the username provided. It is used by Spring Security
     * during the authentication process to validate user credentials.
     * </p>
     *
     * @param username The username of the user.
     * @return A {@link UserDetails} object containing user information.
     * @throws UsernameNotFoundException if a user with the specified username does not exist.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Loads a user by user ID.
     * <p>
     * This method retrieves user details based on the user ID provided. It is used for authorization purposes
     * in cases where user details are required for security context setup.
     * </p>
     *
     * @param id The ID of the user.
     * @return A {@link UserDetails} object containing user information.
     * @throws UsernameNotFoundException if a user with the specified ID does not exist.
     */
    public UserDetails loadUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

}

