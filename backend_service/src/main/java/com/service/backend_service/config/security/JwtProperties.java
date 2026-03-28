package com.service.backend_service.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret) {

    public JwtProperties {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("jwt.secret must be configured with at least 32 characters");
        }
    }
}
