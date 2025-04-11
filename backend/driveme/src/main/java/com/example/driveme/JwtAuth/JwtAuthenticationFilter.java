package com.example.driveme.JwtAuth;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtServices jwtService;
    private final UserDetailsService userDetailsService;
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(
            JwtServices jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        logger.debug("Request path: {}", path);
        // Skip JWT validation for auth endpoints
        return path.startsWith("/auth/");
    }

    @Override
protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
) throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    System.out.println(authHeader.substring(7));
    logger.debug("Processing request to {}, Auth header present: {}", 
            request.getServletPath(), authHeader != null);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        logger.debug("No Bearer token found, continuing filter chain");
        filterChain.doFilter(request, response);
        return;
    }

    try {
        System.out.println(">>> Hit JWT filter!");
        final String jwt = authHeader.substring(7);
        final String identifier = jwtService.extractUsername(jwt);
        logger.debug("JWT token found for user: {}", identifier);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (identifier != null && authentication == null) {
            logger.debug("Loading user details for: {}", identifier);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(identifier);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.debug("Authentication successful for: {}", identifier);
            } else {
                logger.debug("Invalid token for user: {}", identifier);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return; // Stop filter chain
            }
        }

        filterChain.doFilter(request, response);
    } catch (Exception exception) {
        logger.error("Error processing JWT token", exception);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT processing error: " + exception.getMessage());
        // handlerExceptionResolver.resolveException(request, response, null, exception); // Optional: keep if you have a custom resolver
    }
}}