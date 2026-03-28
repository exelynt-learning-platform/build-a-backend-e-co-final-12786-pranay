package com.service.backend_service.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, String previousSecret) {

    public JwtProperties {
        validateSecret(secret);
        if (StringUtils.hasText(previousSecret)) {
            validateSecret(previousSecret);
        }
    }

    private static void validateSecret(String secret) {
        if (!StringUtils.hasText(secret) || secret.length() < 32) {
            throw new IllegalStateException("JWT secret configuration is invalid");
        }
        boolean hasUppercase = secret.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = secret.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = secret.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = secret.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));
        if (!hasUppercase || !hasLowercase || !hasDigit || !hasSpecial) {
            throw new IllegalStateException("JWT secret configuration is invalid");
        }
    }
}
