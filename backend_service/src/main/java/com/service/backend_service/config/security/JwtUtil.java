package com.service.backend_service.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private Key key(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key(jwtProperties.secret()))
                .compact();
    }

    public String extractUsername(String token) {
        for (String secret : candidateSecrets()) {
            try {
                return Jwts.parserBuilder()
                        .setSigningKey(key(secret))
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
            } catch (JwtException ignored) {
                // Try the next configured secret to support key rotation.
            }
        }
        throw new JwtException("JWT token validation failed for all configured secrets");
    }

    private List<String> candidateSecrets() {
        return jwtProperties.previousSecret() == null
                ? List.of(jwtProperties.secret())
                : List.of(jwtProperties.secret(), jwtProperties.previousSecret());
    }
}
