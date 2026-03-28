package com.service.backend_service.config.security;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SecurityConfigTest {

    @Test
    void rejectsWildcardCorsOriginsWhenCredentialsAreEnabled() {
        JwtFilter jwtFilter = org.mockito.Mockito.mock(JwtFilter.class);

        assertThrows(IllegalStateException.class, () -> new SecurityConfig(jwtFilter, "http://localhost:3000,*"));
    }
}
