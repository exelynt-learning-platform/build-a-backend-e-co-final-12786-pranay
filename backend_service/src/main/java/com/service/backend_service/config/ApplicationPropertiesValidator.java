package com.service.backend_service.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ApplicationPropertiesValidator {

    private final String datasourceUsername;
    private final String datasourcePassword;

    public ApplicationPropertiesValidator(
            @Value("${spring.datasource.username:}") String datasourceUsername,
            @Value("${spring.datasource.password:}") String datasourcePassword) {
        this.datasourceUsername = datasourceUsername;
        this.datasourcePassword = datasourcePassword;
    }

    @PostConstruct
    void validate() {
        if (!StringUtils.hasText(datasourceUsername)) {
            throw new IllegalStateException("spring.datasource.username must be configured");
        }
        if (!StringUtils.hasText(datasourcePassword)) {
            throw new IllegalStateException("spring.datasource.password must be configured");
        }
    }
}
