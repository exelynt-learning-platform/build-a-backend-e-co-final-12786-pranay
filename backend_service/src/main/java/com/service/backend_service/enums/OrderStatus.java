package com.service.backend_service.enums;

public enum OrderStatus {
    PENDING("P"),
    CONFIRMED("C"),
    CANCELLED("X");


    private final String code;

    OrderStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
