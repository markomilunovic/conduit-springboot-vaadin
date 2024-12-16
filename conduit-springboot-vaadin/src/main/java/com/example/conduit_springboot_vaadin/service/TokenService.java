package com.example.conduit_springboot_vaadin.service;

import com.example.conduit_springboot_vaadin.common.util.JwtUtil;
import com.example.conduit_springboot_vaadin.model.AccessToken;
import com.example.conduit_springboot_vaadin.model.RefreshToken;
import com.example.conduit_springboot_vaadin.repository.AccessTokenRepository;
import com.example.conduit_springboot_vaadin.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


/**
 * Service responsible for managing and creating access and refresh tokens for user authentication.
 * <p>
 * This service interacts with the database to save tokens associated with a user and handles
 * token expiration details.
 * </p>
 */

@Service
@Transactional
public class TokenService {

    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public TokenService(
            AccessTokenRepository accessTokenRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtUtil jwtUtil
    ) {
        this.accessTokenRepository = accessTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Creates and saves an access token model for a given user.
     * <p>
     * The method initializes an {@link AccessToken} model with the userId, calculates the expiration
     * time based on the access token duration defined in {@link JwtUtil}, and stores the creation
     * and update timestamps. The token is then saved to the database.
     * </p>
     *
     * @param userId The userId for whom the access token is created.
     * @return The created and saved {@link AccessToken} model.
     */
    public AccessToken createAccessTokenModel(String userId) {

        AccessToken accessTokenModel = new AccessToken();

        accessTokenModel.setUserId(userId);
        accessTokenModel.setExpiresAt(LocalDateTime.now().plus(jwtUtil.getAccessTokenExpiration(), ChronoUnit.MILLIS));
        accessTokenModel.setCreatedAt(LocalDateTime.now());
        accessTokenModel.setUpdatedAt(LocalDateTime.now());

        accessTokenModel = accessTokenRepository.save(accessTokenModel);
        return accessTokenModel;

    }

    /**
     * Creates and saves a refresh token model for a given access token.
     * <p>
     * This method initializes a {@link RefreshToken} model, links it to an existing access token,
     * and calculates the expiration time based on the refresh token duration defined in {@link JwtUtil}.
     * It also sets the creation and update timestamps, and then saves the model to the database.
     * </p>
     *
     * @param accessTokenModelId The access token model id associated with the new refresh token.
     * @return The created and saved {@link RefreshToken} model.
     */
    public RefreshToken createRefreshTokenModel(String accessTokenModelId) {

        RefreshToken refreshTokenModel = new RefreshToken();
        refreshTokenModel.setAccessTokenId(accessTokenModelId);
        refreshTokenModel.setExpiresAt(LocalDateTime.now().plus(jwtUtil.getRefreshTokenExpiration(), ChronoUnit.MILLIS));
        refreshTokenModel.setCreatedAt(LocalDateTime.now());
        refreshTokenModel.setUpdatedAt(LocalDateTime.now());

        refreshTokenModel = refreshTokenRepository.save(refreshTokenModel);
        return refreshTokenModel;

    }

}

