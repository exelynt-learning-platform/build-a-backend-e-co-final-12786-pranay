package com.service.backend_service.enums;

public enum PaymentStatus {
    PENDING("P"),
    COMPLETED("C"),
    FAILED("F");
    private final String code;

    PaymentStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
