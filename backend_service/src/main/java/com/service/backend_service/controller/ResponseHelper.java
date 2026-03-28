package com.service.backend_service.controller;

import com.service.backend_service.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ResponseHelper {

    private static final String REQUEST_FAILED_MESSAGE = "Request failed";
    private static final String NOT_FOUND_MESSAGE = "Resource not found";
    private static final String BAD_REQUEST_MESSAGE = "Invalid request";
    private static final String INSUFFICIENT_STORAGE_MESSAGE = "Requested quantity is unavailable";

    private ResponseHelper() {
    }

    public static <T> ResponseEntity<ApiResponse<T>> build(ResponseEntity<T> serviceResponse,
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
            message = NOT_FOUND_MESSAGE;
        } else if (status == HttpStatus.BAD_REQUEST) {
            message = BAD_REQUEST_MESSAGE;
        } else if (status == HttpStatus.INSUFFICIENT_STORAGE) {
            message = INSUFFICIENT_STORAGE_MESSAGE;
        } else {
            message = REQUEST_FAILED_MESSAGE;
        }

        if (body instanceof String stringBody && !stringBody.isBlank()) {
            message = stringBody;
        }

        return ResponseEntity.status(status).body(new ApiResponse<>(false, message, null));
    }
}
