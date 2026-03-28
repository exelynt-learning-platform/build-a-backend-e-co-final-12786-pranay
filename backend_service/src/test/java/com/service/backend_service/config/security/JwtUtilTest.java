package com.service.backend_service.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class JwtUtilTest {

    @Test
    void generateAndExtractUsernameUsesConfiguredSecret() {
        JwtUtil jwtUtil = new JwtUtil(new JwtProperties("12345678901234567890123456789012"));
        jwtUtil.validateSecret();

        String token = jwtUtil.generateToken("alice");

        assertEquals("alice", jwtUtil.extractUsername(token));
    }

    @Test
    void validateSecretRejectsShortSecret() {
        JwtUtil jwtUtil = new JwtUtil(new JwtProperties("short-secret"));

        assertThrows(IllegalStateException.class, jwtUtil::validateSecret);
    }
}
