package com.example.demo.filter;

import com.example.demo.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        // ✅ ADDED: Log the header
        System.out.println("🔍 Authorization Header: " + authorizationHeader);

        String email = null;
        String jwt = null;
        Long userId = null;

        // Extract JWT from Authorization header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("✅ JWT Token Found: " + jwt.substring(0, 20) + "..."); // Print first 20 chars

            try {
                email = jwtUtil.extractEmail(jwt);
                userId = jwtUtil.extractUserId(jwt);
                System.out.println("✅ JWT Parsed - UserId: " + userId + ", Email: " + email);
            } catch (Exception e) {
                logger.error("JWT parsing error: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ No Authorization header or invalid format");
        }

        // Validate token and set authentication
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, email)) {
                System.out.println("✅ JWT VALIDATED SUCCESSFULLY for user: " + userId);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, null,
                        new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("❌ JWT validation failed");
            }
        }

        filterChain.doFilter(request, response);
    }
}