package com.service.backend_service.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ApplicationPropertiesValidatorTest {

    @Test
    void validateRejectsMissingDatasourceUsername() {
        ApplicationPropertiesValidator validator = new ApplicationPropertiesValidator("", "secret");

        assertThrows(IllegalStateException.class, validator::validate);
    }

    @Test
    void validateRejectsMissingDatasourcePassword() {
        ApplicationPropertiesValidator validator = new ApplicationPropertiesValidator("user", "");

        assertThrows(IllegalStateException.class, validator::validate);
    }

    @Test
    void validateAcceptsConfiguredProperties() {
        ApplicationPropertiesValidator validator = new ApplicationPropertiesValidator("user", "secret");

        assertDoesNotThrow(validator::validate);
    }
}
