package com.example.conduit_springboot_vaadin.backend.common.filter;


import com.example.conduit_springboot_vaadin.backend.common.util.JwtUtil;
import com.example.conduit_springboot_vaadin.backend.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * Filter that handles JWT-based authentication for incoming HTTP requests.
 * <p>
 * This filter intercepts each request, checks if a valid JWT is present, and, if valid,
 * sets the user's authentication in the {@link SecurityContextHolder}. It ensures that
 * only authenticated requests proceed to access secured endpoints.
 * </p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   CustomUserDetailsService customUserDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Processes each HTTP request, validates any JWT token present, and sets the user authentication in the security context.
     * <p>
     * This method checks the request for a JWT token in the Authorization header. If a token is present and valid,
     * it loads the user details associated with the token and sets the authentication context for the current request.
     * </p>
     *
     * @param request     The HTTP request to be filtered.
     * @param response    The HTTP response associated with the request.
     * @param filterChain Provides access to the next filter in the chain for further processing.
     * @throws ServletException if an error occurs during filtering.
     * @throws IOException      if an I/O error occurs during filtering.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwt = JwtUtil.getJwtFromRequest(request);

        if (StringUtils.hasText(jwt)) {
            jwtUtil.validateToken(jwt, jwtUtil.getAccessTokenSecret());

            String userId = jwtUtil.getUserIdFromToken(jwt, jwtUtil.getAccessTokenSecret());

            UserDetails userDetails;
            userDetails = customUserDetailsService.loadUserById(userId);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

}

