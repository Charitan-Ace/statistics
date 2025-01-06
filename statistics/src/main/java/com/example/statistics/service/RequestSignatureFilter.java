// com.example.statistics.service.RequestSignatureFilter.java
package com.example.statistics.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Component
public class RequestSignatureFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private static final Logger logger = Logger.getLogger(RequestSignatureFilter.class.getName());

    // Constructor injection for JwtService
    public RequestSignatureFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        logger.info("Authorization Header: " + token);

        if (token != null && token.startsWith("Bearer ")) {
            try {
                String jwt = token.substring(7); // Remove "Bearer " prefix
                Claims claims = jwtService.validateToken(jwt);
                logger.info("JWT Claims: " + claims);

                List<SimpleGrantedAuthority> authorities = jwtService.getAuthoritiesFromToken(jwt); // Extract roles
                logger.info("Extracted Authorities: " + authorities);

                String username = claims.getSubject(); // Extract username from token
                if (username == null) {
                    throw new Exception("JWT token does not contain a subject");
                }

                Authentication authentication = new JwtAuthenticationToken(username, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentication set for user: " + username);
            } catch (Exception e) {
                logger.severe("JWT Validation Failed: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT token.");
                return;
            }
        } else {
            logger.warning("No Authorization header or does not start with Bearer");
        }

        filterChain.doFilter(request, response);
    }
}
