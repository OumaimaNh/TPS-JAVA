package com.auth.jwt.filter;

import com.auth.jwt.service.CustomUserDetailsService;
import com.auth.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter that intercepts HTTP requests to validate JWT tokens.
 * This filter extends OncePerRequestFilter to ensure it's executed once per request.
 * It extracts the JWT token from the Authorization header, validates it, and sets
 * the authentication in the SecurityContext if the token is valid.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Filters incoming requests to validate JWT tokens and set authentication context.
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain to continue processing
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extract Authorization header
            String authHeader = request.getHeader("Authorization");
            
            // Check if header exists and starts with "Bearer "
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            
            // Extract token from "Bearer <token>"
            String token = authHeader.substring(7);
            
            // Validate token
            if (!jwtService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            
            // Extract username from token
            String username = jwtService.extractUsername(token);
            
            // Check if user is not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Create authentication token
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                
                // Set additional details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            
        } catch (Exception e) {
            // Handle exceptions gracefully - log and continue without authentication
            logger.error("Cannot set user authentication: {}", e);
        }
        
        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
