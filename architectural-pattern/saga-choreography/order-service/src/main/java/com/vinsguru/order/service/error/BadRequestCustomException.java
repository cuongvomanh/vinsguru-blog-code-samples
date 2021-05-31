package com.vinsguru.order.service.error;

public class BadRequestCustomException extends RuntimeException {
    public BadRequestCustomException(String message) {
        super(message);
    }
}
