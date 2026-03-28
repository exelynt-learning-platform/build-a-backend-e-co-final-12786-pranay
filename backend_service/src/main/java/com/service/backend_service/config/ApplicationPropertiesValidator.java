package com.service.backend_service.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ApplicationPropertiesValidator {

    private final String datasourceUsername;
    private final String datasourcePassword;
    private final String paymentCallbackToken;

    public ApplicationPropertiesValidator(
            @Value("${spring.datasource.username:}") String datasourceUsername,
            @Value("${spring.datasource.password:}") String datasourcePassword,
            @Value("${payment.callback.token:}") String paymentCallbackToken) {
        this.datasourceUsername = datasourceUsername;
        this.datasourcePassword = datasourcePassword;
        this.paymentCallbackToken = paymentCallbackToken;
    }

    @PostConstruct
    void validate() {
        if (!StringUtils.hasText(datasourceUsername)) {
            throw new IllegalStateException("spring.datasource.username must be configured");
        }
        if (!StringUtils.hasText(datasourcePassword)) {
            throw new IllegalStateException("spring.datasource.password must be configured");
        }
        if (!StringUtils.hasText(paymentCallbackToken)) {
            throw new IllegalStateException("payment.callback.token must be configured");
        }
    }
}
