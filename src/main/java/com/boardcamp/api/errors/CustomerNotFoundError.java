package com.boardcamp.api.errors;

public class CustomerNotFoundError extends RuntimeException {
    public CustomerNotFoundError(String message) {
        super(message);
    }
}
