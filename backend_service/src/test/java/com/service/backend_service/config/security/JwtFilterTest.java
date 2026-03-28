package com.service.backend_service.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtFilterTest {

    @Test
    void skipsAuthenticationForConfiguredExcludedPath() throws Exception {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtFilter jwtFilter = new JwtFilter(jwtUtil, "/swagger-ui,/v3/api-docs,/auth");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getServletPath()).thenReturn("/swagger-ui/index.html");

        jwtFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    void authenticatesBearerTokenForNonExcludedPath() throws Exception {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtFilter jwtFilter = new JwtFilter(jwtUtil, "/swagger-ui,/v3/api-docs,/auth");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getServletPath()).thenReturn("/products/1");
        when(request.getHeader("Authorization")).thenReturn("Bearer token-123");
        when(jwtUtil.extractUsername("token-123")).thenReturn("alice");

        jwtFilter.doFilterInternal(request, response, chain);

        verify(jwtUtil).extractUsername("token-123");
        verify(chain).doFilter(request, response);
        assertEquals("alice", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        SecurityContextHolder.clearContext();
    }
}
