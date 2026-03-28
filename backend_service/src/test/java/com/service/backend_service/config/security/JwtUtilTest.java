package com.service.backend_service.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    @Test
    void generateAndExtractUsernameUsesConfiguredSecret() {
        JwtUtil jwtUtil = new JwtUtil(new JwtProperties("Abcdefghijklmnopqrstuvwx123456!@", null));

        String token = jwtUtil.generateToken("alice");

        assertEquals("alice", jwtUtil.extractUsername(token));
    }

    @Test
    void validateSecretRejectsShortSecret() {
        IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> new JwtProperties("short-secret", null));
        assertEquals("JWT secret configuration is invalid", exception.getMessage());
    }

    @Test
    void validateSecretRejectsWeakSecretWithoutRequiredCharacterClasses() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> new JwtProperties("abcdefghijklmnopqrstuvwxyz123456", null)
        );
        assertEquals("JWT secret configuration is invalid", exception.getMessage());
    }

    @Test
    void extractUsernameAcceptsPreviousSecretDuringRotation() {
        JwtUtil previousJwtUtil = new JwtUtil(new JwtProperties("Abcdefghijklmnopqrstuvwx123456!@", null));
        String token = previousJwtUtil.generateToken("alice");
        JwtUtil rotatedJwtUtil = new JwtUtil(
                new JwtProperties("Zyxwvutsrqponmlkjihgfedc654321!@", "Abcdefghijklmnopqrstuvwx123456!@")
        );

        assertEquals("alice", rotatedJwtUtil.extractUsername(token));
    }

    @Test
    void extractUsernameRejectsTokenWhenNoConfiguredSecretMatches() {
        JwtUtil signingJwtUtil = new JwtUtil(new JwtProperties("Abcdefghijklmnopqrstuvwx123456!@", null));
        String token = signingJwtUtil.generateToken("alice");
        JwtUtil validatingJwtUtil = new JwtUtil(new JwtProperties("Zyxwvutsrqponmlkjihgfedc654321!@", null));

        assertThrows(JwtException.class, () -> validatingJwtUtil.extractUsername(token));
    }
}
