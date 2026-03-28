package com.service.backend_service.controller;

import com.service.backend_service.dto.ApiResponse;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseHelper {

    private final MessageSource messageSource;

    public ResponseHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public <T> ResponseEntity<ApiResponse<T>> build(ResponseEntity<T> serviceResponse,
                                                    String successMessage) {
        HttpStatus status = HttpStatus.valueOf(serviceResponse.getStatusCode().value());
        T body = serviceResponse.getBody();

        if (status.is2xxSuccessful()) {
            String message = successMessage;
            if (body instanceof String stringBody && !stringBody.isBlank()) {
                message = stringBody;
                body = null;
            }
            return ResponseEntity.status(status).body(new ApiResponse<>(true, message, body));
        }

        String message;
        if (status == HttpStatus.NOT_FOUND) {
            message = message("response.not_found");
        } else if (status == HttpStatus.BAD_REQUEST) {
            message = message("response.bad_request");
        } else if (status == HttpStatus.INSUFFICIENT_STORAGE) {
            message = message("response.insufficient_storage");
        } else {
            message = message("response.request_failed");
        }

        if (body instanceof String stringBody && !stringBody.isBlank()) {
            message = stringBody;
        }

        return ResponseEntity.status(status).body(new ApiResponse<>(false, message, null));
    }

    private String message(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }
}
